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

import com.ccteam.graphql.entities.EventReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link EventReminder} repository.
 *
 * @author yann39
 * @since 1.0.2
 */
@Repository
public interface EventReminderRepository extends JpaRepository<EventReminder, Long> {

    /**
     * Check whether a reminder has already been sent for the given event and offset.
     *
     * @param eventId   The event id
     * @param offsetKey The reminder offset key
     * @return {@code true} when the reminder was already sent, {@code false} otherwise
     */
    boolean existsByEventIdAndOffsetKey(long eventId, String offsetKey);

    /**
     * Delete all the sent-reminder stamps of the given event. Used when an event is rescheduled,
     * so its reminders fire again for the new date, and when an event is deleted.
     *
     * @param eventId The event id
     */
    @Transactional
    void deleteByEventId(long eventId);

}
