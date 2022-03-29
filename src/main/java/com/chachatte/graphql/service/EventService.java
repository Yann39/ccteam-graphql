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

package com.chachatte.graphql.service;

import com.chachatte.graphql.config.graphql.CustomGraphQLException;
import com.chachatte.graphql.entities.Event;
import com.chachatte.graphql.entities.Member;
import com.chachatte.graphql.entities.News;
import com.chachatte.graphql.entities.Track;
import com.chachatte.graphql.repository.EventRepository;
import com.chachatte.graphql.repository.MemberRepository;
import com.chachatte.graphql.repository.TrackRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * {@link News} service.
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
     * Get an event given its {@code id}.
     *
     * @param id The ID of the event to retrieve
     * @return An {@link Event} object representing the event
     */
    public Event getEventById(Long id) {
        final Optional<Event> eventOptional = eventRepository.findByIdCustom(id);
        if (eventOptional.isEmpty()) {
            log.error("Event with id " + id + " not found in the database");
            throw new CustomGraphQLException("event_not_found", "Specified member has not been found in the database");
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
    public Event createEvent(@Argument String title, @Argument String description, @Argument String startDate,
                             @Argument String endDate, @Argument long trackId, @Argument String organizer,
                             @Argument BigDecimal price, @Argument long memberId) {
        final Optional<Track> track = trackRepository.findByIdCustom(trackId);
        if (track.isEmpty()) {
            log.error("Track with id " + trackId + " not found in the database");
            throw new CustomGraphQLException("track_not_found", "Specified track ID has not been found in the database");
        }

        final Optional<Member> member = memberRepository.findByIdCustom(memberId);
        if (member.isEmpty()) {
            log.error("Member with id " + memberId + " not found in the database");
            throw new CustomGraphQLException("member_not_found", "Specified member ID has not been found in the database");
        }

        final Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        event.setStartDate(LocalDateTime.parse(startDate));
        event.setEndDate(LocalDateTime.parse(endDate));
        event.setTrack(track.get());
        event.setOrganizer(organizer);
        event.setPrice(price);
        event.setCreatedBy(member.get());
        event.setCreatedOn(LocalDateTime.now());
        return eventRepository.save(event);
    }

}