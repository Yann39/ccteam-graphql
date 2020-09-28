package com.chachatte.graphql.repository;

import com.chachatte.graphql.entities.News;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author yann39
 * @since sept 2020
 */
public interface NewsRepository extends PagingAndSortingRepository<News, Long> {

    List<News> findByTitle(@Param("title") String title);

}
