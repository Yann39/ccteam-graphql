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

package com.chachatte.graphql.graphql.mutation;

import com.chachatte.graphql.dto.EventDto;
import com.chachatte.graphql.entities.Event;
import com.chachatte.graphql.repository.EventRepository;
import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

/**
 * @author yann39
 * @since sept 2020
 */
@Component
public class EventMutationResolver implements GraphQLMutationResolver {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;

    public EventMutationResolver(EventRepository eventRepository, ModelMapper modelMapper) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Create a new event.
     *
     * @param title       The event title
     * @param description The event description
     * @return A DTO object representing the event just created
     */
    @Secured("ROLE_ADMIN")
    public EventDto newEvent(String title, String description) {
        final Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        final Event created = eventRepository.save(event);
        return convertToDto(created);
    }

    /**
     * Convert the specified {@link Event} object to an {@link EventDto} object
     *
     * @param event The event to convert
     * @return The {@link EventDto} object created
     */
    private EventDto convertToDto(Event event) {
        return modelMapper.map(event, EventDto.class);
    }
}