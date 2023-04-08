/*
 * Copyright (c) 2022 by Yann39
 *
 * This file is part of Chachatte Team GraphQL application.
 *
 * Chachatte Team GraphQL is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Chachatte Team GraphQL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with Chachatte Team GraphQL. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.chachatte.graphql.repository;

import com.chachatte.graphql.entities.News;
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

    @Query("select distinct n from News n " +
            "left join fetch n.likedNews ln " +
            "left join fetch n.createdBy " +
            "left join fetch n.modifiedBy " +
            "left join fetch ln.member")
    List<News> findAllCustom();

    @EntityGraph(attributePaths = {"likedNews", "createdBy", "modifiedBy", "likedNews.member"})
    @Query("select distinct n from News n")
    Page<News> findFilteredCustom(Example<News> example, Pageable pageable);

    @Query("select n from News n " +
            "left join fetch n.likedNews ln " +
            "left join fetch n.createdBy " +
            "left join fetch n.modifiedBy " +
            "left join fetch ln.news " +
            "left join fetch ln.member " +
            "where n.id = :id")
    Optional<News> findByIdCustom(long id);

    @Modifying
    @Transactional
    @Query(value = "insert into liked_news(member_id, news_id, created_on) values (:memberId, :newsId, :createdOn)", nativeQuery = true)
    int likeNews(long memberId, long newsId, LocalDateTime createdOn);

    @Modifying
    @Transactional
    @Query(value = "delete from liked_news where member_id = :memberId and news_id = :newsId", nativeQuery = true)
    int unlikeNews(long memberId, long newsId);

}
