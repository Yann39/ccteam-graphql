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

package com.ccteam.graphql.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Stamp of a maintenance reminder push notification already sent, so each reminder is sent exactly once.
 * <p>
 * Reminders count from the last maintenance of the bike, hence the key: once a new maintenance is recorded, the
 * next reminders are keyed on it and fire again.
 *
 * @author yann39
 * @since 1.3.0
 */
@Getter
@Setter
@Entity
@Table(name = "maintenance_reminder", uniqueConstraints = @UniqueConstraint(columnNames = {"maintenance_id", "kind"}))
public class MaintenanceReminder {

    /**
     * Database identifier (primary key) for this reminder.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID of the bike the reminder was sent for.
     */
    @Column(name = "bike_id", nullable = false)
    private Long bikeId;

    /**
     * ID of the last {@link Maintenance} of the bike at the time the reminder was sent.
     */
    @Column(name = "maintenance_id", nullable = false)
    private Long maintenanceId;

    /**
     * Kind of reminder ({@code DUE_SOON} or {@code OVERDUE}).
     */
    @Column(length = 16, nullable = false)
    private String kind;

    /**
     * Timestamp when the reminder notification was sent.
     */
    @Column(nullable = false)
    private LocalDateTime sentOn;

}
