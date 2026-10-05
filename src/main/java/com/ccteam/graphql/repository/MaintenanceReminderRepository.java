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

package com.ccteam.graphql.repository;

import com.ccteam.graphql.entities.MaintenanceReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link MaintenanceReminder} repository.
 *
 * @author yann39
 * @since 1.3.0
 */
@Repository
public interface MaintenanceReminderRepository extends JpaRepository<MaintenanceReminder, Long> {

    /**
     * Check whether a reminder of the given kind has already been sent for the given last maintenance.
     *
     * @param maintenanceId The id of the last maintenance of the bike
     * @param kind          The reminder kind
     * @return {@code true} when the reminder was already sent, {@code false} otherwise
     */
    boolean existsByMaintenanceIdAndKind(long maintenanceId, String kind);

    /**
     * Delete all the sent-reminder stamps of the given bike. Used when its plan or odometer changes, so reminders
     * are evaluated again against the new values, and when the bike is deleted.
     *
     * @param bikeId The bike id
     */
    @Transactional
    void deleteByBikeId(long bikeId);

}
