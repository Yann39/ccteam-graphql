/*
 * Copyright (c) 2024 by Yann39
 *
 * This file is part of CCTeam GraphQL application.
 *
 * CCTeam GraphQL is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * CCTeam GraphQL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with CCTeam GraphQL. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.ccteam.graphql.service;

import com.ccteam.graphql.config.graphql.CustomGraphQLException;
import com.ccteam.graphql.entities.Bike;
import com.ccteam.graphql.entities.Maintenance;
import com.ccteam.graphql.entities.MaintenanceOperation;
import com.ccteam.graphql.entities.MaintenancePlan;
import com.ccteam.graphql.enums.CurrencyCode;
import com.ccteam.graphql.enums.MaintenanceDueStatus;
import com.ccteam.graphql.enums.MaintenanceOperationType;
import com.ccteam.graphql.model.BikeMaintenanceView;
import com.ccteam.graphql.model.MaintenanceOperationInput;
import com.ccteam.graphql.repository.BikeRepository;
import com.ccteam.graphql.repository.MaintenancePlanRepository;
import com.ccteam.graphql.repository.MaintenanceReminderRepository;
import com.ccteam.graphql.repository.MaintenanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Bike maintenance service: maintenance plan, maintenances and odometer of a bike.
 * <p>
 * Maintenance data is private, every operation checks that the bike belongs to the authenticated member.
 *
 * @author yann39
 * @since 1.3.0
 */
@Service
@Slf4j
public class MaintenanceService {

    /**
     * A maintenance is due soon when it is due within this number of days...
     */
    static final int DUE_SOON_DAYS = 30;

    /**
     * ...or within this fraction of the plan distance interval (e.g. 500 km for a 5000 km plan).
     */
    static final int DUE_SOON_KM_DIVISOR = 10;

    private final BikeRepository bikeRepository;
    private final MaintenancePlanRepository maintenancePlanRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final MaintenanceReminderRepository maintenanceReminderRepository;

    public MaintenanceService(BikeRepository bikeRepository,
                              MaintenancePlanRepository maintenancePlanRepository,
                              MaintenanceRepository maintenanceRepository,
                              MaintenanceReminderRepository maintenanceReminderRepository) {
        this.bikeRepository = bikeRepository;
        this.maintenancePlanRepository = maintenancePlanRepository;
        this.maintenanceRepository = maintenanceRepository;
        this.maintenanceReminderRepository = maintenanceReminderRepository;
    }

    /**
     * Get the maintenance data of the given bike.
     *
     * @param bikeId The bike id
     * @param email  The e-mail of the authenticated member, who must own the bike
     * @return The maintenance data of the bike
     */
    @Transactional(readOnly = true)
    public BikeMaintenanceView getBikeMaintenance(Long bikeId, String email) {
        return buildView(getOwnedBike(bikeId, email));
    }

    /**
     * Define or replace the maintenance plan of the given bike.
     *
     * @param bikeId         The bike id
     * @param intervalKm     Distance between two maintenances, in kilometers (optional)
     * @param intervalMonths Time between two maintenances, in months (optional)
     * @param email          The e-mail of the authenticated member, who must own the bike
     * @return The updated maintenance data of the bike
     */
    @Transactional
    public BikeMaintenanceView setMaintenancePlan(Long bikeId, Integer intervalKm, Integer intervalMonths,
                                                  String email) {
        final Bike bike = getOwnedBike(bikeId, email);
        if (intervalKm == null && intervalMonths == null) {
            throw new CustomGraphQLException("maintenance_plan_empty",
                    "A maintenance plan needs a distance interval, a time interval or both");
        }
        if ((intervalKm != null && intervalKm <= 0) || (intervalMonths != null && intervalMonths <= 0)) {
            throw new CustomGraphQLException("maintenance_plan_invalid", "Maintenance plan intervals must be positive");
        }

        final MaintenancePlan plan = maintenancePlanRepository.findByBikeId(bikeId).orElseGet(() -> {
            final MaintenancePlan created = new MaintenancePlan();
            created.setBike(bike);
            created.setCreatedOn(LocalDateTime.now());
            return created;
        });
        if (plan.getId() != null) {
            plan.setModifiedOn(LocalDateTime.now());
        }
        plan.setIntervalKm(intervalKm);
        plan.setIntervalMonths(intervalMonths);
        maintenancePlanRepository.save(plan);

        // the due date moved, let the reminders be evaluated again
        maintenanceReminderRepository.deleteByBikeId(bikeId);

        return buildView(bike);
    }

    /**
     * Delete the maintenance plan of the given bike, the maintenances are kept.
     *
     * @param bikeId The bike id
     * @param email  The e-mail of the authenticated member, who must own the bike
     * @return The updated maintenance data of the bike
     */
    @Transactional
    public BikeMaintenanceView deleteMaintenancePlan(Long bikeId, String email) {
        final Bike bike = getOwnedBike(bikeId, email);
        maintenancePlanRepository.findByBikeId(bikeId).ifPresent(maintenancePlanRepository::delete);
        maintenanceReminderRepository.deleteByBikeId(bikeId);
        return buildView(bike);
    }

    /**
     * Record a new maintenance for the given bike.
     *
     * @param bikeId          The bike id
     * @param maintenanceDate The maintenance date as ISO 8601 string
     * @param odometerKm      The odometer reading at the time of the maintenance (optional)
     * @param resetsPlan      Whether the maintenance restarts the plan countdown ({@code null} means yes)
     * @param comment         A free comment (optional)
     * @param operations      The operations performed, at least one
     * @param email           The e-mail of the authenticated member, who must own the bike
     * @return The updated maintenance data of the bike
     */
    @Transactional
    public BikeMaintenanceView createMaintenance(Long bikeId, String maintenanceDate, Integer odometerKm,
                                                 Boolean resetsPlan, String comment,
                                                 List<MaintenanceOperationInput> operations,
                                                 String email) {
        final Bike bike = getOwnedBike(bikeId, email);

        final Maintenance maintenance = new Maintenance();
        maintenance.setBike(bike);
        maintenance.setCreatedOn(LocalDateTime.now());
        fillMaintenance(maintenance, maintenanceDate, odometerKm, resetsPlan, comment, operations);
        maintenanceRepository.save(maintenance);

        updateOdometerFromMaintenance(bike, maintenance);

        return buildView(bike);
    }

    /**
     * Update an existing maintenance.
     *
     * @param maintenanceId   The maintenance id
     * @param maintenanceDate The maintenance date as ISO 8601 string
     * @param odometerKm      The odometer reading at the time of the maintenance (optional)
     * @param resetsPlan      Whether the maintenance restarts the plan countdown ({@code null} means yes)
     * @param comment         A free comment (optional)
     * @param operations      The operations performed, at least one, replacing the previous ones
     * @param email           The e-mail of the authenticated member, who must own the bike
     * @return The updated maintenance data of the bike
     */
    @Transactional
    public BikeMaintenanceView updateMaintenance(Long maintenanceId, String maintenanceDate, Integer odometerKm,
                                                 Boolean resetsPlan, String comment,
                                                 List<MaintenanceOperationInput> operations,
                                                 String email) {
        final Maintenance maintenance = getOwnedMaintenance(maintenanceId, email);
        final Bike bike = maintenance.getBike();

        fillMaintenance(maintenance, maintenanceDate, odometerKm, resetsPlan, comment, operations);
        maintenance.setModifiedOn(LocalDateTime.now());
        maintenanceRepository.save(maintenance);

        updateOdometerFromMaintenance(bike, maintenance);
        maintenanceReminderRepository.deleteByBikeId(bike.getId());

        return buildView(bike);
    }

    /**
     * Delete a maintenance.
     *
     * @param maintenanceId The maintenance id
     * @param email         The e-mail of the authenticated member, who must own the bike
     * @return The updated maintenance data of the bike
     */
    @Transactional
    public BikeMaintenanceView deleteMaintenance(Long maintenanceId, String email) {
        final Maintenance maintenance = getOwnedMaintenance(maintenanceId, email);
        final Bike bike = maintenance.getBike();
        maintenanceRepository.delete(maintenance);
        maintenanceRepository.flush();
        maintenanceReminderRepository.deleteByBikeId(bike.getId());
        return buildView(bike);
    }

    /**
     * Set the current odometer reading of the given bike.
     *
     * @param bikeId     The bike id
     * @param odometerKm The odometer reading, in kilometers
     * @param email      The e-mail of the authenticated member, who must own the bike
     * @return The updated maintenance data of the bike
     */
    @Transactional
    public BikeMaintenanceView updateBikeOdometer(Long bikeId, Integer odometerKm, String email) {
        final Bike bike = getOwnedBike(bikeId, email);
        if (odometerKm == null || odometerKm < 0) {
            throw new CustomGraphQLException("odometer_invalid", "The odometer reading must be a positive number");
        }
        bike.setOdometerKm(odometerKm);
        bike.setOdometerUpdatedOn(LocalDateTime.now());
        bikeRepository.save(bike);
        return buildView(bike);
    }

    /**
     * Delete all the maintenance data of a bike, before the bike itself gets deleted.
     *
     * @param bikeId The bike id
     */
    @Transactional
    public void deleteBikeMaintenanceData(Long bikeId) {
        maintenanceReminderRepository.deleteByBikeId(bikeId);
        maintenanceRepository.deleteAll(maintenanceRepository.findByBikeId(bikeId));
        maintenancePlanRepository.findByBikeId(bikeId).ifPresent(maintenancePlanRepository::delete);
    }

    /**
     * Compute where the bike stands relative to its next maintenance: the next maintenance is due
     * {@code intervalMonths} after the last service, or {@code intervalKm} after the odometer reading of the last
     * service, whichever comes first. Maintenances that don't reset the plan (a tire...) are ignored.
     *
     * @param plan            The maintenance plan, may be {@code null}
     * @param lastMaintenance The last service of the plan ({@link Maintenance#getResetsPlan()}), may be {@code null}
     * @param odometerKm      The current odometer reading of the bike, may be {@code null}
     * @return The next maintenance due date and mileage
     */
    BikeMaintenanceView.Due computeNextDue(MaintenancePlan plan, Maintenance lastMaintenance, Integer odometerKm) {
        if (plan == null || lastMaintenance == null) {
            return new BikeMaintenanceView.Due(MaintenanceDueStatus.UNKNOWN, null, null, null, null);
        }

        LocalDateTime dueDate = null;
        Long remainingDays = null;
        if (plan.getIntervalMonths() != null) {
            dueDate = lastMaintenance.getMaintenanceDate().plusMonths(plan.getIntervalMonths());
            remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), dueDate.toLocalDate());
        }

        Integer dueKm = null;
        Integer remainingKm = null;
        if (plan.getIntervalKm() != null && lastMaintenance.getOdometerKm() != null) {
            dueKm = lastMaintenance.getOdometerKm() + plan.getIntervalKm();
            if (odometerKm != null) {
                remainingKm = dueKm - odometerKm;
            }
        }

        final MaintenanceDueStatus status;
        if (remainingDays == null && remainingKm == null) {
            status = MaintenanceDueStatus.UNKNOWN;
        } else if ((remainingDays != null && remainingDays < 0) || (remainingKm != null && remainingKm <= 0)) {
            status = MaintenanceDueStatus.OVERDUE;
        } else if ((remainingDays != null && remainingDays <= DUE_SOON_DAYS)
                || (remainingKm != null && remainingKm <= plan.getIntervalKm() / DUE_SOON_KM_DIVISOR)) {
            status = MaintenanceDueStatus.DUE_SOON;
        } else {
            status = MaintenanceDueStatus.OK;
        }

        return new BikeMaintenanceView.Due(status, dueDate, dueKm, remainingDays, remainingKm);
    }

    /**
     * Compute where the bike stands relative to its next maintenance, loading what is needed. Used by the reminder
     * job, which has no authenticated member.
     *
     * @param plan The maintenance plan, with its bike
     * @return The next maintenance due date and mileage, along with the last maintenance it counts from
     */
    @Transactional(readOnly = true)
    public NextDue computeNextDue(MaintenancePlan plan) {
        final Bike bike = plan.getBike();
        final Maintenance last = maintenanceRepository
                .findFirstByBikeIdAndResetsPlanTrueOrderByMaintenanceDateDescIdDesc(bike.getId())
                .orElse(null);
        return new NextDue(last != null ? last.getId() : null, computeNextDue(plan, last, bike.getOdometerKm()));
    }

    /**
     * The next maintenance due of a bike, along with the id of the last maintenance it counts from.
     *
     * @param lastMaintenanceId The id of the last maintenance, {@code null} when none was recorded
     * @param due               The next maintenance due date and mileage
     */
    public record NextDue(Long lastMaintenanceId, BikeMaintenanceView.Due due) {
    }

    private void fillMaintenance(Maintenance maintenance, String maintenanceDate, Integer odometerKm,
                                 Boolean resetsPlan, String comment, List<MaintenanceOperationInput> operations) {
        if (operations == null || operations.isEmpty()) {
            throw new CustomGraphQLException("maintenance_no_operation", "A maintenance needs at least one operation");
        }
        if (odometerKm != null && odometerKm < 0) {
            throw new CustomGraphQLException("odometer_invalid", "The odometer reading must be a positive number");
        }

        maintenance.setMaintenanceDate(LocalDateTime.parse(maintenanceDate));
        maintenance.setOdometerKm(odometerKm);
        maintenance.setResetsPlan(resetsPlan == null || resetsPlan);
        maintenance.setComment(comment == null || comment.isBlank() ? null : comment.trim());

        // replace the operations wholesale, they are small and have no identity of their own for the member
        maintenance.getOperations().clear();
        int position = 0;
        for (final MaintenanceOperationInput input : operations) {
            final String label = input.label() == null || input.label().isBlank() ? null : input.label().trim();
            if (input.type() == MaintenanceOperationType.OTHER && label == null) {
                throw new CustomGraphQLException("maintenance_operation_label_required",
                        "An operation of type OTHER needs a label");
            }
            final MaintenanceOperation operation = new MaintenanceOperation();
            operation.setMaintenance(maintenance);
            operation.setPosition(position++);
            operation.setType(input.type());
            operation.setLabel(label);
            operation.setPrice(input.price() != null
                    ? BigDecimal.valueOf(input.price()).setScale(2, RoundingMode.HALF_UP)
                    : null);
            operation.setCurrency(input.currency() != null ? input.currency() : CurrencyCode.CHF);
            maintenance.getOperations().add(operation);
        }
    }

    /**
     * Move the bike odometer forward when a maintenance carries a higher reading than the last known one.
     * Never moves it backwards, so entering an old maintenance afterwards does not lose the current reading.
     */
    private void updateOdometerFromMaintenance(Bike bike, Maintenance maintenance) {
        final Integer km = maintenance.getOdometerKm();
        if (km != null && (bike.getOdometerKm() == null || km > bike.getOdometerKm())) {
            bike.setOdometerKm(km);
            bike.setOdometerUpdatedOn(maintenance.getMaintenanceDate());
            bikeRepository.save(bike);
        }
    }

    private BikeMaintenanceView buildView(Bike bike) {
        final MaintenancePlan plan = maintenancePlanRepository.findByBikeId(bike.getId()).orElse(null);
        final List<Maintenance> maintenances = maintenanceRepository.findByBikeIdWithOperations(bike.getId());

        final List<BikeMaintenanceView.Maintenance> maintenanceViews = maintenances.stream()
                .map(m -> new BikeMaintenanceView.Maintenance(
                        m.getId(),
                        m.getMaintenanceDate(),
                        m.getOdometerKm(),
                        m.getResetsPlan(),
                        m.getComment(),
                        m.getOperations().stream()
                                .map(o -> new BikeMaintenanceView.Operation(o.getId(), o.getType(), o.getLabel(),
                                        o.getPrice(), o.getCurrency()))
                                .toList(),
                        totals(m)))
                .toList();

        return new BikeMaintenanceView(
                bike.getId(),
                bike.getOdometerKm(),
                bike.getOdometerUpdatedOn(),
                plan != null
                        ? new BikeMaintenanceView.Plan(plan.getId(), plan.getIntervalKm(), plan.getIntervalMonths())
                        : null,
                maintenanceViews,
                // maintenances are sorted most recent first, the plan counts from the most recent service
                computeNextDue(plan,
                        maintenances.stream().filter(Maintenance::getResetsPlan).findFirst().orElse(null),
                        bike.getOdometerKm()));
    }

    /**
     * Sum of the operation prices, one amount per currency (in the {@link CurrencyCode} order), empty when no
     * operation has a price.
     */
    private static List<BikeMaintenanceView.Amount> totals(Maintenance maintenance) {
        return Arrays.stream(CurrencyCode.values())
                .map(currency -> maintenance.getOperations().stream()
                        .filter(o -> o.getCurrency() == currency)
                        .map(MaintenanceOperation::getPrice)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal::add)
                        .map(amount -> new BikeMaintenanceView.Amount(currency, amount))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }

    private Bike getOwnedBike(Long bikeId, String email) {
        final Bike bike = bikeRepository.findById(bikeId).orElseThrow(() -> {
            log.error("Bike with id {} not found", bikeId);
            return new CustomGraphQLException("bike_not_found", "Specified bike has not been found");
        });
        checkOwnership(bike, email);
        return bike;
    }

    private Maintenance getOwnedMaintenance(Long maintenanceId, String email) {
        final Maintenance maintenance = maintenanceRepository.findById(maintenanceId).orElseThrow(() -> {
            log.error("Maintenance with id {} not found", maintenanceId);
            return new CustomGraphQLException("maintenance_not_found", "Specified maintenance has not been found");
        });
        checkOwnership(maintenance.getBike(), email);
        return maintenance;
    }

    private static void checkOwnership(Bike bike, String email) {
        if (bike.getMember() == null || !bike.getMember().getEmail().equalsIgnoreCase(email)) {
            log.warn("Member {} tried to access the maintenance data of bike {} which is not theirs", email,
                    bike.getId());
            throw new CustomGraphQLException("bike_not_owned", "Maintenance data is only available to the bike owner");
        }
    }

}
