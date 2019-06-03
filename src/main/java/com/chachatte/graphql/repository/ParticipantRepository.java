package com.chachatte.graphql.repository;

import com.chachatte.graphql.domain.Event;
import com.chachatte.graphql.domain.Participant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author yann39
 * @since may 2019
 */
public interface ParticipantRepository extends PagingAndSortingRepository<Participant, Long> {

    @Query("SELECT p FROM Participant p WHERE p.email=:email and p.event=:event")
    Participant findByEmailAndEvent(@Param("email") String email, @Param("event") Event event);

    List<Participant> findByFirstNameOrLastNameOrEmail(@Param("text") String firstName, @Param("text") String lastName, @Param("text") String email);
}
