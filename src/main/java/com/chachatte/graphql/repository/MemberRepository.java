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

import com.chachatte.graphql.entities.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * {@link Member} repository.
 *
 * @author yann39
 * @since 1.0.0
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("select distinct m from Member m " +
            "left join fetch m.eventMembers em " +
            "left join fetch em.event " +
            "left join fetch m.likedNews ln " +
            "left join fetch ln.news")
    List<Member> findAllCustom();

    @Query("select m from Member m " +
            "left join fetch m.eventMembers em " +
            "left join fetch em.event " +
            "left join fetch m.likedNews ln " +
            "left join fetch ln.news " +
            "where m.id = :id")
    Optional<Member> findByIdCustom(long id);

    @Query("select m from Member m " +
            "left join fetch m.eventMembers em " +
            "left join fetch em.event " +
            "left join fetch m.likedNews ln " +
            "left join fetch ln.news " +
            "where m.email = :email")
    Optional<Member> findByEmailCustom(String email);

    @Query("select distinct m from Member m " +
            "left join fetch m.eventMembers em " +
            "left join fetch em.event " +
            "left join fetch m.likedNews ln " +
            "left join fetch ln.news " +
            "where :text is null or ( " +
            "m.firstName like %:text% " +
            "or m.lastName like %:text% " +
            "or m.email like %:text%" +
            ")")
    List<Member> findFilteredCustom(String text);

    boolean existsMemberByEmail(String email);
}
