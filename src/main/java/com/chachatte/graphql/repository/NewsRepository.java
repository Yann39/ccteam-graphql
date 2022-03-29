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
import com.chachatte.graphql.projection.NewsDetailsProjection;
import com.chachatte.graphql.projection.NewsListProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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

    @Query("select n from News n " +
            "left join fetch n.likedNews ln " +
            "left join fetch n.createdBy " +
            "left join fetch n.modifiedBy " +
            "left join fetch ln.member " +
            "where n.id = :id")
    Optional<News> findByIdCustom(long id);

    @Query(value = "select n.id, n.title, n.catch_line as catchLine, n.news_date as newsDate " +
            "from news n", nativeQuery = true)
    List<NewsListProjection> findAllCustomForHomeList();

    @Query(value = "select n.id, n.title, n.content, n.news_date as newsDate, concat(m.first_name, ' ', m.last_name) as createdBy from news n " +
            "inner join member m on m.id = n.created_by " +
            "where n.id = :id", nativeQuery = true)
    Optional<NewsDetailsProjection> findOneCustomForDetailsView(long id);

}
