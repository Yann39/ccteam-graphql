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

package com.ccteam.graphql.controller.graphql;

import com.ccteam.graphql.config.graphql.CustomGraphQLException;
import com.ccteam.graphql.entities.Event;
import com.ccteam.graphql.entities.Member;
import com.ccteam.graphql.entities.Track;
import com.ccteam.graphql.service.EventService;
import com.ccteam.graphql.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    private final MemberService memberService;

    public EventController(EventService eventService, MemberService memberService) {
        this.eventService = eventService;
        this.memberService = memberService;
    }

    /**
     * Get all events.
     *
     * @return A list of {@link Event} objects representing the events
     */
    @PreAuthorize("hasRole('USER')")
    @QueryMapping
    public List<Event> getAllEvents() {
        log.info("Received call to getAllEvents");
        return eventService.getAllEvents();
    }

    /**
     * Lightweight count of all events in the database. USER-accessible
     * even when the caller is not yet a fully-validated MEMBER.
     */
    @PreAuthorize("hasRole('USER')")
    @QueryMapping
    public Long getEventsCount() {
        log.info("Received call to getEventsCount");
        return eventService.getEventsCount();
    }

    /**
     * Get all events in the specified year, based on event start date.
     *
     * @param year The year of the events to retrieve
     * @return A list of {@link Event} objects representing the events
     */
    @PreAuthorize("hasRole('USER')")
    @QueryMapping
    public List<Event> getEventsByYear(@Argument int year) {
        log.info("Received call to getEventsByYear with parameter year = {}", year);
        return eventService.getEventsByYear(year);
    }

    /**
     * Get all events in the specified month and year, based on event start date.
     *
     * @param month The month of the events to retrieve as integer from 1 to 12
     *              (January to December)
     * @param year  The year of the events to retrieve
     * @return A list of {@link Event} objects representing the events
     */
    @PreAuthorize("hasRole('USER')")
    @QueryMapping
    public List<Event> getEventsByMonthAndYear(@Argument int month,
                                               @Argument int year) {
        log.info("Received call to getEventsByMonthAndYear with parameters month = {}, year = {}", month, year);
        return eventService.getEventsByMonthAndYear(month, year);
    }

    /**
     * Get all events in the specified day, month and year, based on event start
     * date.
     *
     * @param day   The day of the events to retrieve
     * @param month The month of the events to retrieve as integer from 1 to 12
     *              (January to December)
     * @param year  The year of the events to retrieve
     * @return A list of {@link Event} objects representing the events
     */
    @PreAuthorize("hasRole('USER')")
    @QueryMapping
    public List<Event> getEventsByDayAndMonthAndYear(@Argument int day,
                                                     @Argument int month,
                                                     @Argument int year) {
        log.info("Received call to getEventsByDayAndMonthAndYear with parameters day = {}, month = {}, year = {}", day,
                month, year);
        return eventService.getEventsByDayAndMonthAndYear(day, month, year);
    }

    /**
     * Get an event given its {@code id}.
     *
     * @param id The ID of the event to retrieve
     * @return An {@link Event} object representing the event
     */
    @PreAuthorize("hasRole('USER')")
    @QueryMapping
    public Event getEventById(@Argument Long id) {
        log.info("Received call to getEventById with parameter ID = {}", id);
        return eventService.getEventById(id);
    }

    /**
     * Get all events with the specified {@code title}.
     *
     * @param title The event title
     * @return A list of {@link Event} objects representing the events
     */
    @PreAuthorize("hasRole('USER')")
    @QueryMapping
    public List<Event> getEventsByTitle(@Argument String title) {
        log.info("Received call to getEventsByTitle with parameter title = {}", title);
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
     * @param organizerId The ID of the {@link com.ccteam.graphql.entities.Organizer} of the event
     * @param price       The event price
     * @param memberId    The ID of the {@link Member} who created that event
     * @return An {@link Event} object representing the event just created
     */
    @PreAuthorize("hasRole('ADMIN')")
    @MutationMapping
    public Event createEvent(@Argument String title,
                             @Argument String description,
                             @Argument String startDate,
                             @Argument String endDate,
                             @Argument long trackId,
                             @Argument long organizerId,
                             @Argument BigDecimal price,
                             @Argument long memberId) {
        log.info(
                "Received call to createEvent with parameters title = {}, description = {}, startDate = {}, endDate = {}, trackId = {}, organizerId = {}, price = {}, memberId = {}",
                title, description, startDate, endDate, trackId, organizerId, price, memberId);
        return eventService.createEvent(title, description, startDate, endDate, trackId, organizerId, price, memberId);
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
     * @param organizerId The ID of the {@link com.ccteam.graphql.entities.Organizer} of the event
     * @param price       The event price
     * @param memberId    The ID of the {@link Member} who created that event
     * @return An {@link Event} object representing the event just updated
     */
    @PreAuthorize("hasRole('ADMIN')")
    @MutationMapping
    public Event updateEvent(@Argument long eventId,
                             @Argument String title,
                             @Argument String description,
                             @Argument String startDate,
                             @Argument String endDate,
                             @Argument long trackId,
                             @Argument long organizerId,
                             @Argument BigDecimal price,
                             @Argument long memberId) {
        log.info(
                "Received call to updateEvent with parameters eventId = {}, title = {}, description = {}, startDate = {}, endDate = {}, trackId = {}, organizerId = {}, price = {}, memberId = {}",
                eventId, title, description, startDate, endDate, trackId, organizerId, price, memberId);
        return eventService.updateEvent(eventId, title, description, startDate, endDate, trackId, organizerId, price,
                memberId);
    }

    /**
     * Delete the event represented by the given event ID.
     *
     * @param eventId The ID of the {@link Event} to delete
     * @return A {@link Event} object representing the event just deleted
     */
    @PreAuthorize("hasRole('ADMIN')")
    @MutationMapping
    public Event deleteEvent(@Argument long eventId) {
        log.info("Received call to deleteEvent with parameter eventId = {}", eventId);
        return eventService.deleteEvent(eventId);
    }

    /**
     * Mark the specified event as registered by the specified member.
     *
     * @param eventId  The event ID
     * @param memberId The member ID
     * @param bikeId   Optional bike id to pin to the participation (may
     *                 be {@code null}). When non-null, the bike must
     *                 belong to {@code memberId}.
     * @return An {@link Event} object representing the event just registered
     */
    @PreAuthorize("hasRole('MEMBER')")
    @MutationMapping
    public Event registerToEvent(@Argument long eventId,
                                 @Argument long memberId,
                                 @Argument Long bikeId) {
        log.info("Received call to registerToEvent with parameters eventId = {}, memberId = {}, bikeId = {}",
                eventId, memberId, bikeId);
        return eventService.registerToEvent(eventId, memberId, bikeId);
    }

    /**
     * Mark the specified event as not registered by the specified member.
     *
     * @param eventId  The event ID
     * @param memberId The member ID
     * @return An {@link Event} object representing the event just unregistered
     */
    @PreAuthorize("hasRole('MEMBER')")
    @MutationMapping
    public Event unregisterFromEvent(@Argument long eventId, @Argument long memberId) {
        log.info("Received call to unregisterFromEvent with parameters eventId = {}, memberId = {}", eventId, memberId);
        return eventService.unregisterFromEvent(eventId, memberId);
    }

    /**
     * Change (or clear, by passing a {@code null} bike) the bike pinned
     * to the caller's own participation in event {@code eventId}.
     * <p>
     * The acting member is derived from {@link Authentication} (JWT
     * subject) rather than taken as a separate argument — a member can
     * only edit their own participation, not anyone else's, so there's
     * no need to surface a {@code memberId} parameter that an admin
     * dashboard would otherwise be tempted to spoof. Keeps the mutation
     * intent unambiguous on the wire.
     *
     * @param eventId        the event id
     * @param bikeId         the bike id to pin, or {@code null} to clear
     * @param authentication the current authentication (auto-injected)
     * @return the updated {@link Event}
     */
    @PreAuthorize("hasRole('MEMBER')")
    @MutationMapping
    public Event setEventMemberBike(@Argument long eventId,
                                    @Argument Long bikeId,
                                    Authentication authentication) {
        log.info("Received call to setEventMemberBike with parameters eventId = {}, bikeId = {}", eventId, bikeId);
        final Member caller = memberService.getMemberByEmail(authentication.getName());
        if (caller == null || caller.getId() == null) {
            throw new CustomGraphQLException("member_not_found", "Authenticated member could not be resolved");
        }
        return eventService.setEventMemberBike(eventId, caller.getId(), bikeId);
    }

}