/*
 * Copyright (c) 2020 by Yann39.
 *
 * This file is part of Chachatte Team application.
 *
 * Chachatte Team is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Chachatte Team is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Chachatte Team. If not, see <http://www.gnu.org/licenses/>.
 */

package com.chachatte.graphql.repository;

import com.chachatte.graphql.entities.LikedNews;
import com.chachatte.graphql.entities.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author yann39
 * @since sept 2020
 */
public interface LikedNewsRepository extends JpaRepository<LikedNews, Long> {

    @Query("select ln from LikedNews ln where ln.news.id = ?1 and ln.member.id = ?2")
    LikedNews findByNewsIdAndMemberId(@Param("newsId") long newsId, @Param("memberId") long memberId);

    @Query("select case when count(ln) > 0 then true else false end from LikedNews ln where ln.news.id = ?1 and ln.member.id = ?2")
    boolean existsByNewsIdAndMemberId(@Param("newsId") long newsId, @Param("memberId") long memberId);

}
