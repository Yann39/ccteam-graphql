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

package com.ccteam.graphql.model;

import com.ccteam.graphql.enums.CurrencyCode;
import com.ccteam.graphql.enums.MaintenanceDueStatus;
import com.ccteam.graphql.enums.MaintenanceOperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Read-only view of everything maintenance-related for one bike, exposed over GraphQL to the bike owner only.
 * <p>
 * Built inside the service transaction so no lazy relation is left to resolve once the response is serialized.
 *
 * @param bikeId            The bike id
 * @param odometerKm        Last known odometer reading, in kilometers
 * @param odometerUpdatedOn Date of the last known odometer reading
 * @param plan              The maintenance plan, {@code null} when none is defined
 * @param maintenances      The maintenances, most recent first
 * @param nextDue           The next maintenance due date and mileage
 * @author yann39
 * @since 1.3.0
 */
public record BikeMaintenanceView(
        Long bikeId,
        Integer odometerKm,
        LocalDateTime odometerUpdatedOn,
        Plan plan,
        List<Maintenance> maintenances,
        Due nextDue
) {

    public record Plan(
            Long id,
            Integer intervalKm,
            Integer intervalMonths
    ) {
    }

    public record Maintenance(
            Long id,
            LocalDateTime maintenanceDate,
            Integer odometerKm,
            Boolean resetsPlan,
            String comment,
            List<Operation> operations,
            List<Amount> totals
    ) {
    }

    public record Operation(
            Long id,
            MaintenanceOperationType type,
            String label,
            BigDecimal price,
            CurrencyCode currency
    ) {
    }

    /**
     * An amount of money. Amounts in different currencies are never converted nor added together.
     */
    public record Amount(
            CurrencyCode currency,
            BigDecimal amount
    ) {
    }

    /**
     * @param status        Where the bike stands relative to its next maintenance
     * @param dueDate       Date at which the next maintenance is due, {@code null} when not time-based
     * @param dueKm         Odometer reading at which the next maintenance is due, {@code null} when unknown
     * @param remainingDays Days left until {@code dueDate}, negative when exceeded
     * @param remainingKm   Kilometers left until {@code dueKm}, negative when exceeded, {@code null} when the
     *                      current odometer reading is unknown
     */
    public record Due(
            MaintenanceDueStatus status,
            LocalDateTime dueDate,
            Integer dueKm,
            Long remainingDays,
            Integer remainingKm
    ) {
    }

}
