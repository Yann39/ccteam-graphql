package com.chachatte.graphql.resolver;

import com.chachatte.graphql.domain.Event;
import com.chachatte.graphql.domain.Participant;
import com.chachatte.graphql.repository.ParticipantRepository;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Component;

/**
 * @author yann39
 * @since may 2019
 */
@Component
public class ParticipantQueryResolver implements GraphQLQueryResolver {

    private final ParticipantRepository participantRepository;

    public ParticipantQueryResolver(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public Iterable<Participant> getAllParticipants() {
        return participantRepository.findAll();
    }

    public Participant getParticipantByEmailAndEvent(String email, Event event) {
        return participantRepository.findByEmailAndEvent(email, event);
    }

    public Iterable<Participant> getParticipantByFirstNameOrLastNameOrEmail(String text) {
        return participantRepository.findByFirstNameOrLastNameOrEmail(text, text, text);
    }
}