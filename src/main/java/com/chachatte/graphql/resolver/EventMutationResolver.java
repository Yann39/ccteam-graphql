package com.chachatte.graphql.resolver;

import com.chachatte.graphql.domain.Event;
import com.chachatte.graphql.repository.EventRepository;
import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import org.springframework.stereotype.Component;

/**
 * @author yann39
 * @since may 2019
 */
@Component
public class EventMutationResolver implements GraphQLMutationResolver {

    private final EventRepository eventRepository;

    public EventMutationResolver(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event newEvent(String titleFr, String titleEn, String email) {
        Event event = new Event();
        event.setTitleFr(titleFr);
        event.setTitleEn(titleEn);
        event.setEmail(email);
        return eventRepository.save(event);
    }
}