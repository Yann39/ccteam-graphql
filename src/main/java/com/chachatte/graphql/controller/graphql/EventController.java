/*
 * Copyright (c) 2022 by Yann39
 *
 * This file is part of Chachatte Team GraphQL application.
 *
 * Chachatte Team GraphQL is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Chachatte Team GraphQL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with Chachatte Team GraphQL. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.chachatte.graphql.controller.graphql;

import com.chachatte.graphql.entities.Event;
import com.chachatte.graphql.entities.Member;
import com.chachatte.graphql.entities.Track;
import com.chachatte.graphql.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.List;

/**
 * {@link Event} GraphQL controller.
 *
 * @author yann39
 * @since 1.0.0
 */
@Controller
@Slf4j
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Get all events.
     *
     * @return A list of {@link Event} objects representing the events
     */
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @QueryMapping
    public List<Event> getAllEvents() {
        log.info("Received call to getAllEvents");
        return eventService.getAllEvents();
    }

    /**
     * Get an event given its {@code id}.
     *
     * @param id The ID of the event to retrieve
     * @return An {@link Event} object representing the event
     */
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @QueryMapping
    public Event getEventById(@Argument Long id) {
        log.info("Received call to getEventById with parameter ID = " + id);
        return eventService.getEventById(id);
    }

    /**
     * Get all events with the specified {@code title}.
     *
     * @param title The event title
     * @return A list of {@link Event} objects representing the events
     */
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @QueryMapping
    public List<Event> getEventsByTitle(@Argument String title) {
        log.info("Received call to getEventsByTitle with parameter title = " + title);
        return eventService.getEventsByTitle(title);
    }

    /**
     * Create a new event.
     *
     * @param title       The event title
     * @param description The event description
     * @param startDate   The start date of the event as ISO {@link String}
     * @param endDate     The end date of the event as ISO {@link String}
     * @param trackId     The ID of the {@link Track} where the event takes place
     * @param organizer   The organizer of the event
     * @param price       The event price
     * @param memberId    The ID of the {@link Member} who created that event
     * @return An {@link Event} object representing the event just created
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @MutationMapping
    public Event createEvent(@Argument String title, @Argument String description, @Argument String startDate,
                             @Argument String endDate, @Argument long trackId, @Argument String organizer,
                             @Argument BigDecimal price, @Argument long memberId) {
        log.info("Received call to createEvent with parameters title = " + title + ", description = " + description +
                ", startDate = " + startDate + ", endDate = " + endDate + ", trackId = " + trackId + ", organizer = " + organizer +
                ", price = " + price + ", memberId = " + memberId);
        return eventService.createEvent(title, description, startDate, endDate, trackId, organizer, price, memberId);
    }

    /**
     * Update the event represented by the given event ID with the specified data.
     *
     * @param eventId     The ID of the {@link Event} to update
     * @param title       The event title
     * @param description The event description
     * @param startDate   The start date of the event as ISO {@link String}
     * @param endDate     The end date of the event as ISO {@link String}
     * @param trackId     The ID of the {@link Track} where the event takes place
     * @param organizer   The organizer of the event
     * @param price       The event price
     * @param memberId    The ID of the {@link Member} who created that event
     * @return An {@link Event} object representing the event just updated
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @MutationMapping
    public Event updateEvent(@Argument long eventId, @Argument String title, @Argument String description,
                             @Argument String startDate, @Argument String endDate, @Argument long trackId,
                             @Argument String organizer, @Argument BigDecimal price, @Argument long memberId) {
        log.info("Received call to updateEvent with parameters eventId = " + eventId + ", title = " + title + ", description = " + description +
                ", startDate = " + startDate + ", endDate = " + endDate + ", trackId = " + trackId + ", organizer = " + organizer +
                ", price = " + price + ", memberId = " + memberId);
        return eventService.updateEvent(eventId, title, description, startDate, endDate, trackId, organizer, price, memberId);
    }

    /**
     * Delete the event represented by the given event ID.
     *
     * @param eventId The ID of the {@link Event} to delete
     * @return A {@link Event} object representing the event just deleted
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @MutationMapping
    public Event deleteEvent(@Argument long eventId) {
        log.info("Received call to deleteNews with parameter eventId = " + eventId);
        return eventService.deleteEvent(eventId);
    }

}