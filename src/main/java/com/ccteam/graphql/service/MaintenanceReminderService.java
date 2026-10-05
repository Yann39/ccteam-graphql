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

import com.ccteam.graphql.entities.Bike;
import com.ccteam.graphql.entities.MaintenancePlan;
import com.ccteam.graphql.entities.MaintenanceReminder;
import com.ccteam.graphql.enums.MaintenanceDueStatus;
import com.ccteam.graphql.model.BikeMaintenanceView;
import com.ccteam.graphql.repository.MaintenancePlanRepository;
import com.ccteam.graphql.repository.MaintenanceReminderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Scheduled job sending push notification reminders when a bike maintenance is approaching or overdue.
 * <p>
 * Every bike with a maintenance plan gets at most one "due soon" and one "overdue" reminder per maintenance
 * period, sent on its own FCM topic ({@code bike-{id}-maintenance}) to which only the owner's devices subscribe.
 * Sent reminders are stamped in the {@code maintenance_reminder} table, keyed on the last maintenance of the bike,
 * so recording a new maintenance starts a new period. A failed send is not stamped and is retried on the next run.
 * <p>
 * Reminders are only sent during the day, nobody wants to be woken up about an oil change.
 *
 * @author yann39
 * @since 1.3.0
 */
@Service
@Slf4j
public class MaintenanceReminderService {

    private static final LocalTime SEND_FROM = LocalTime.of(9, 0);
    private static final LocalTime SEND_UNTIL = LocalTime.of(20, 0);

    private final MaintenancePlanRepository maintenancePlanRepository;
    private final MaintenanceReminderRepository maintenanceReminderRepository;
    private final MaintenanceService maintenanceService;
    private final PushNotificationService pushNotificationService;

    public MaintenanceReminderService(MaintenancePlanRepository maintenancePlanRepository,
                                      MaintenanceReminderRepository maintenanceReminderRepository,
                                      MaintenanceService maintenanceService,
                                      PushNotificationService pushNotificationService) {
        this.maintenancePlanRepository = maintenancePlanRepository;
        this.maintenanceReminderRepository = maintenanceReminderRepository;
        this.maintenanceService = maintenanceService;
        this.pushNotificationService = pushNotificationService;
    }

    /**
     * Send the due maintenance reminders. Runs every hour, a reminder is therefore sent at most one hour after
     * the bike enters the reminder window (or the next morning).
     */
    @Scheduled(initialDelay = 2, fixedDelay = 60, timeUnit = TimeUnit.MINUTES)
    public void sendMaintenanceReminders() {
        if (!pushNotificationService.isEnabled()) {
            return;
        }
        final LocalTime now = LocalTime.now();
        if (now.isBefore(SEND_FROM) || now.isAfter(SEND_UNTIL)) {
            return;
        }

        for (final MaintenancePlan plan : maintenancePlanRepository.findAllWithBike()) {
            final MaintenanceService.NextDue nextDue = maintenanceService.computeNextDue(plan);
            final BikeMaintenanceView.Due due = nextDue.due();
            if (nextDue.lastMaintenanceId() == null
                    || (due.status() != MaintenanceDueStatus.DUE_SOON && due.status() != MaintenanceDueStatus.OVERDUE)) {
                continue;
            }

            final String kind = due.status().name();
            if (maintenanceReminderRepository.existsByMaintenanceIdAndKind(nextDue.lastMaintenanceId(), kind)) {
                continue;
            }

            final Bike bike = plan.getBike();
            log.info("Sending {} maintenance reminder for bike {}", kind, bike.getId());

            final boolean sent = pushNotificationService.sendToTopic(
                    PushNotificationService.TOPIC_BIKE_PREFIX + bike.getId() + PushNotificationService.TOPIC_MAINTENANCE_SUFFIX,
                    "Entretien : " + bike.getManufacturer() + " " + bike.getModelName(),
                    buildBody(due),
                    Map.of("type", "bike", "id", String.valueOf(bike.getId())));

            // only stamp on success so failed sends are retried on the next run
            if (sent) {
                final MaintenanceReminder reminder = new MaintenanceReminder();
                reminder.setBikeId(bike.getId());
                reminder.setMaintenanceId(nextDue.lastMaintenanceId());
                reminder.setKind(kind);
                reminder.setSentOn(LocalDateTime.now());
                maintenanceReminderRepository.save(reminder);
            }
        }
    }

    /**
     * Build the notification body, e.g. "Prochain entretien dans 450 km ou 12 jours" or
     * "Entretien dépassé de 200 km".
     */
    static String buildBody(BikeMaintenanceView.Due due) {
        final List<String> parts = new ArrayList<>();
        if (due.status() == MaintenanceDueStatus.OVERDUE) {
            if (due.remainingKm() != null && due.remainingKm() <= 0) {
                parts.add(-due.remainingKm() + " km");
            }
            if (due.remainingDays() != null && due.remainingDays() < 0) {
                parts.add(-due.remainingDays() + (due.remainingDays() == -1 ? " jour" : " jours"));
            }
            return parts.isEmpty() ? "L'entretien est à faire." : "Entretien dépassé de " + String.join(" et ", parts) + ".";
        }
        if (due.remainingKm() != null) {
            parts.add(due.remainingKm() + " km");
        }
        if (due.remainingDays() != null) {
            parts.add(due.remainingDays() + (due.remainingDays() == 1 ? " jour" : " jours"));
        }
        return "Prochain entretien dans " + String.join(" ou ", parts) + ".";
    }

}
