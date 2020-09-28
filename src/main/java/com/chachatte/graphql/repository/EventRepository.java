package com.chachatte.graphql.repository;

import com.chachatte.graphql.entities.Event;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author yann39
 * @since sept 2020
 */
public interface EventRepository extends PagingAndSortingRepository<Event, Long> {

    List<Event> findByTitle(@Param("title") String title);

}
