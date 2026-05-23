/*
 * Copyright (c) 2024 by Yann39
 *
 * This file is part of CCTeam GraphQL application.
 *
 * CCTeam GraphQL is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * CCTeam GraphQL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with CCTeam GraphQL. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.ccteam.graphql.repository;

import com.ccteam.graphql.entities.News;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * {@link News} repository.
 *
 * @author yann39
 * @since 1.0.0
 */
@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    /**
     * Retrieve all news items with related likes and authors fetched.
     * Results are ordered by newsDate descending (newest first).
     *
     * @return The list of news items with related associations fetched
     */
    @Query("select distinct n from News n " +
           "left join fetch n.likedNews ln " +
           "left join fetch n.createdBy " +
           "left join fetch n.modifiedBy " +
           "left join fetch ln.member " +
           "order by n.newsDate desc")
    List<News> findAllCustom();

    /**
     * Find news items matching the example, returning a paginated result.
     * The entity graph ensures likes and author/modifier are fetched.
     *
     * @param example  The example probe used for filtering
     * @param pageable The pagination information
     * @return The paged news items matching the example
     */
    @EntityGraph(attributePaths = {"likedNews", "createdBy", "modifiedBy", "likedNews.member"})
    @Query("select distinct n from News n " +
           "order by n.newsDate desc")
    Page<News> findFilteredCustom(Example<News> example, Pageable pageable);

    /**
     * Find a news item by id and eagerly fetch likes and related member references to provide a complete object
     * graph for display.
     *
     * @param id The news id
     * @return The optional news with associations fetched
     */
    @Query("select n from News n " +
           "left join fetch n.likedNews ln " +
           "left join fetch n.createdBy " +
           "left join fetch n.modifiedBy " +
           "left join fetch ln.news " +
           "left join fetch ln.member " +
           "where n.id = :id")
    Optional<News> findByIdCustom(long id);

    /**
     * Create a like relation for the given member and news using a native insert for performance.
     *
     * @return The number of rows inserted (should be 1 when successful)
     */
    @Modifying
    @Transactional
    @Query(value = "insert into liked_news(member_id, news_id, created_on) values (:memberId, :newsId, :createdOn)", nativeQuery = true)
    int likeNews(long memberId, long newsId, LocalDateTime createdOn);

    /**
     * Remove the like relation for the given member and news.
     *
     * @return The number of rows deleted (0 or 1)
     */
    @Modifying
    @Transactional
    @Query(value = "delete from liked_news where member_id = :memberId and news_id = :newsId", nativeQuery = true)
    int unlikeNews(long memberId, long newsId);

}
