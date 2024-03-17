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
import com.ccteam.graphql.entities.Event;
import com.ccteam.graphql.entities.Member;
import com.ccteam.graphql.entities.Track;
import com.ccteam.graphql.repository.EventRepository;
import com.ccteam.graphql.repository.MemberRepository;
import com.ccteam.graphql.repository.TrackRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * {@link Event} service.
 *
 * @author yann39
 * @since 1.0.0
 */
@Service
@Slf4j
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private MemberRepository memberRepository;

    /**
     * Get all events.
     *
     * @return A list of {@link Event} objects representing the events
     */
    public List<Event> getAllEvents() {
        return eventRepository.findAllCustom();
    }

    /**
     * Get all events in the specified year, based on event start date.
     *
     * @param year The year of the events to retrieve
     * @return A list of {@link Event} objects representing the events
     */
    public List<Event> getEventsByYear(int year) {
        return eventRepository.findByYearCustom(year);
    }

    /**
     * Get all events in the specified month and year, based on event start date.
     *
     * @param month The month of the events to retrieve as integer from 1 to 12 (January to December)
     * @param year  The year of the events to retrieve
     * @return A list of {@link Event} objects representing the events
     */
    public List<Event> getEventsByMonthAndYear(int month, int year) {
        return eventRepository.findByMonthAndYearCustom(month, year);
    }

    /**
     * Get all events in the specified day, month and year, based on event start date.
     *
     * @param day   The day of the events to retrieve
     * @param month The month of the events to retrieve as integer from 1 to 12 (January to December)
     * @param year  The year of the events to retrieve
     * @return A list of {@link Event} objects representing the events
     */
    public List<Event> getEventsByDayAndMonthAndYear(int day, int month, int year) {
        return eventRepository.findByDayAndMonthAndYearCustom(day, month, year);
    }

    /**
     * Get an event given its {@code id}.
     *
     * @param id The ID of the event to retrieve
     * @return An {@link Event} object representing the event
     */
    public Event getEventById(Long id) {
        final Optional<Event> eventOptional = eventRepository.findByIdCustom(id);
        if (eventOptional.isEmpty()) {
            log.error("Event with id {} not found in the database", id);
            throw new CustomGraphQLException("event_not_found", "Specified event has not been found in the database");
        }
        return eventOptional.get();
    }

    /**
     * Get all events with the specified {@code title}.
     *
     * @param title The event title
     * @return A list of {@link Event} objects representing the events
     */
    public List<Event> getEventsByTitle(String title) {
        return eventRepository.findByTitleCustom(title);
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
    public Event createEvent(String title, String description, String startDate, String endDate, long trackId, String organizer, BigDecimal price, long memberId) {
        final Optional<Track> trackOptional = trackRepository.findByIdCustom(trackId);
        if (trackOptional.isEmpty()) {
            log.error("Track with id {} not found in the database", trackId);
            throw new CustomGraphQLException("track_not_found", "Specified track ID has not been found in the database");
        }

        final Optional<Member> memberOptional = memberRepository.findByIdCustom(memberId);
        if (memberOptional.isEmpty()) {
            log.error("Member with id {} not found in the database", memberId);
            throw new CustomGraphQLException("member_not_found", "Specified member ID has not been found in the database");
        }

        final Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        event.setStartDate(LocalDateTime.parse(startDate));
        event.setEndDate(LocalDateTime.parse(endDate));
        event.setTrack(trackOptional.get());
        event.setOrganizer(organizer);
        event.setPrice(price);
        event.setCreatedBy(memberOptional.get());
        event.setCreatedOn(LocalDateTime.now());
        return eventRepository.save(event);
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
    public Event updateEvent(long eventId, String title, String description, String startDate, String endDate, long trackId, String organizer, BigDecimal price, long memberId) {
        final Optional<Event> eventOptional = eventRepository.findByIdCustom(eventId);
        if (eventOptional.isEmpty()) {
            log.error("Event with id {} not found in the database", eventId);
            throw new CustomGraphQLException("event_not_found", "Specified event ID has not been found in the database");
        }

        final Optional<Track> trackOptional = trackRepository.findByIdCustom(trackId);
        if (trackOptional.isEmpty()) {
            log.error("Track with id {} not found in the database", trackId);
            throw new CustomGraphQLException("track_not_found", "Specified track ID has not been found in the database");
        }

        final Optional<Member> memberOptional = memberRepository.findByIdCustom(memberId);
        if (memberOptional.isEmpty()) {
            log.error("Member with id {} not found in the database", memberId);
            throw new CustomGraphQLException("member_not_found", "Specified member ID has not been found in the database");
        }

        final Event event = eventOptional.get();
        event.setTitle(title);
        event.setDescription(description);
        event.setStartDate(LocalDateTime.parse(startDate));
        event.setEndDate(LocalDateTime.parse(endDate));
        event.setTrack(trackOptional.get());
        event.setOrganizer(organizer);
        event.setPrice(price);
        event.setModifiedBy(memberOptional.get());
        event.setModifiedOn(LocalDateTime.now());
        return eventRepository.save(event);
    }

    /**
     * Delete the event represented by the given event ID.
     *
     * @param eventId The ID of the {@link Event} to delete
     * @return A {@link Event} object representing the event just deleted
     */
    public Event deleteEvent(long eventId) {
        final Optional<Event> eventOptional = eventRepository.findByIdCustom(eventId);
        if (eventOptional.isEmpty()) {
            log.error("Event with id {} not found in the database", eventId);
            throw new CustomGraphQLException("event_not_found", "Specified event ID has not been found in the database");
        }

        final Event event = eventOptional.get();
        eventRepository.delete(event);
        return event;
    }

}