package com.chachatte.graphql.repository;

import com.chachatte.graphql.domain.Event;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author yann39
 * @since may 2019
 */
public interface EventRepository extends PagingAndSortingRepository<Event, Long> {

    List<Event> findByTitleFr(@Param("titleFr") String titleFr);

}
