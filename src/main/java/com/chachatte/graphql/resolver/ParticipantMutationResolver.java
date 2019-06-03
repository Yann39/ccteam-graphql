package com.chachatte.graphql.resolver;

import com.chachatte.graphql.domain.Participant;
import com.chachatte.graphql.repository.ParticipantRepository;
import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import org.springframework.stereotype.Component;

/**
 * @author yann39
 * @since may 2019
 */
@Component
public class ParticipantMutationResolver implements GraphQLMutationResolver {

    private final ParticipantRepository participantRepository;

    public ParticipantMutationResolver(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public Participant newParticipant(String firstName, String lastName, String email) {
        Participant participant = new Participant();
        participant.setFirstName(firstName);
        participant.setLastName(lastName);
        participant.setEmail(email);
        return participantRepository.save(participant);
    }
}