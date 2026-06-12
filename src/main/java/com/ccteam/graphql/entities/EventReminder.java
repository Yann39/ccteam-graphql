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
 * Reminder push notification already sent for an event at a given offset
 * before its start ({@link com.ccteam.graphql.enums.ReminderOffset}).
 * <p>
 * One row per (event, offset) pair, used by the scheduled reminder job to
 * guarantee each reminder is sent exactly once. The event is referenced by
 * its plain id (no foreign key) so deleting an event never conflicts with
 * its sent-reminder history; leftover rows of deleted events are harmless.
 *
 * @author yann39
 * @since 1.0.2
 */
@Getter
@Setter
@Entity
@Table(name = "event_reminder", uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "offset_key"}))
public class EventReminder {

    /**
     * Database identifier (primary key) for this reminder.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID of the {@link Event} the reminder was sent for.
     */
    @Column(name = "event_id", nullable = false)
    private Long eventId;

    /**
     * Key of the reminder offset ({@link com.ccteam.graphql.enums.ReminderOffset}).
     */
    @Column(name = "offset_key", length = 8, nullable = false)
    private String offsetKey;

    /**
     * Timestamp when the reminder notification was sent.
     */
    @Column(nullable = false)
    private LocalDateTime sentOn;

}
