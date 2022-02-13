/*
 * Copyright (c) 2020 by Yann39.
 *
 * This file is part of Chachatte Team application.
 *
 * Chachatte Team is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Chachatte Team is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Chachatte Team. If not, see <http://www.gnu.org/licenses/>.
 */

package com.chachatte.graphql.graphql.query;

import com.chachatte.graphql.dto.EventDto;
import com.chachatte.graphql.dto.MemberDto;
import com.chachatte.graphql.entities.Event;
import com.chachatte.graphql.entities.Member;
import com.chachatte.graphql.repository.EventRepository;
import com.chachatte.graphql.repository.MemberRepository;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author yann39
 * @since sept 2020
 */
@Component
@Slf4j
public class EventQueryResolver implements GraphQLQueryResolver {

    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    public EventQueryResolver(EventRepository eventRepository, MemberRepository memberRepository, ModelMapper modelMapper) {
        this.eventRepository = eventRepository;
        this.memberRepository = memberRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Get all events.
     *
     * @return A list of DTO objects representing the events
     */
    @Secured("ROLE_MEMBER")
    public Iterable<EventDto> getAllEvents() {
        log.info("Received call to getAllEvents");
        final List<Event> result = new ArrayList<>();
        eventRepository.findAll().forEach(result::add);
        return result.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    /**
     * Get an event given the specified {@code id}.
     *
     * @param id The event ID
     * @return A list of DTO objects representing the events
     */
    @Secured("ROLE_MEMBER")
    public EventDto getEventById(Long id) {
        log.info("Received call to getEventById with parameter ID = " + id);
        final Optional<Event> event = eventRepository.findById(id);
        return event.map(this::convertToDto).orElse(null);
    }

    /**
     * Get all events with the specified {@code title}.
     *
     * @param title The event title
     * @return A list of DTO objects representing the events
     */
    @Secured("ROLE_MEMBER")
    public Iterable<EventDto> getEventsByTitle(String title) {
        log.info("Received call to getEventsByTitle with parameter title = " + title);
        return eventRepository.findByTitle(title).stream().map(this::convertToDto).collect(Collectors.toList());
    }

    /**
     * Convert the specified {@link Event} entity object to an {@link EventDto} DTO object.
     *
     * @param event The event to convert
     * @return The {@link EventDto} object created
     */
    private EventDto convertToDto(Event event) {
        final EventDto eventDto = modelMapper.map(event, EventDto.class);
        final List<MemberDto> memberDtos = memberRepository.findByEventId(eventDto.getId()).stream().map(this::convertToMemberDto).collect(Collectors.toList());
        eventDto.setMembers(new HashSet<>(memberDtos));
        return eventDto;
    }

    /**
     * Convert the specified {@link Member} entity object to a {@link MemberDto} DTO object.
     *
     * @param member The member to convert
     * @return The {@link MemberDto} object created
     */
    private MemberDto convertToMemberDto(Member member) {
        return modelMapper.map(member, MemberDto.class);
    }

}