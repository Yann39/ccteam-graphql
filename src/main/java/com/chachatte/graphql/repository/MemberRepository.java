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

import com.chachatte.graphql.entities.Member;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * @author yann39
 * @since sept 2020
 */
public interface MemberRepository extends PagingAndSortingRepository<Member, Long> {

    List<Member> findByFirstNameLikeOrLastNameLikeOrEmailLike(@Param("text") String firstName, @Param("text") String lastName, @Param("text") String email);

    @Query("select m from Member m inner join LikedNews ln on m.id = ln.member.id where ln.news.id = ?1")
    List<Member> findByNewsId(@Param("newsId") long newsId);

    Member findByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    Optional<Member> findByEmail(@Param("email") String email);

    boolean existsMemberByEmail(String email);
}
