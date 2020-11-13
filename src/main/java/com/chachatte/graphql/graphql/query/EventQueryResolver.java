package com.chachatte.graphql.graphql.query;

import com.chachatte.graphql.dto.EventDto;
import com.chachatte.graphql.entities.Event;
import com.chachatte.graphql.repository.EventRepository;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yann39
 * @since sept 2020
 */
@Component
@Slf4j
public class EventQueryResolver implements GraphQLQueryResolver {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;

    public EventQueryResolver(EventRepository eventRepository, ModelMapper modelMapper) {
        this.eventRepository = eventRepository;
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
     * Convert the specified {@link Event} object to an {@link EventDto} object.
     *
     * @param event The event to convert
     * @return The {@link EventDto} object created
     */
    private EventDto convertToDto(Event event) {
        return modelMapper.map(event, EventDto.class);
    }

}