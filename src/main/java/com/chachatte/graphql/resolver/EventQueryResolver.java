package com.chachatte.graphql.resolver;

import com.chachatte.graphql.domain.Event;
import com.chachatte.graphql.repository.EventRepository;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Component;

/**
 * @author yann39
 * @since may 2019
 */
@Component
public class EventQueryResolver implements GraphQLQueryResolver {

    private final EventRepository eventRepository;

    public EventQueryResolver(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Iterable<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Iterable<Event> getEventByTitleFr(String titleFr) {
        return eventRepository.findByTitleFr(titleFr);
    }
}