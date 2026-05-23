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

import com.ccteam.graphql.entities.Attachment;
import com.ccteam.graphql.entities.Member;
import com.ccteam.graphql.enums.BoardRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    /**
     * Get all members from the database, with required fetched relations.
     *
     * @return The list of all members
     */
    @Query("select distinct m from Member m " +
            "left join fetch m.eventMembers em " +
            "left join fetch em.event e " +
            "left join fetch e.organizer " +
            "left join fetch m.likedNews ln " +
            "left join fetch ln.news " +
            "left join fetch m.bikes " +
            "left join fetch m.membershipFees " +
            "order by m.firstName, m.lastName")
    List<Member> findAllCustom();

    /**
     * Get a members given its ID, with required fetched relations.
     *
     * @return The optional member with the specified ID
     */
    @Query("select m from Member m " +
            "left join fetch m.avatar " +
            "left join fetch m.eventMembers em " +
            "left join fetch em.event e " +
            "left join fetch e.participants " +
            "left join fetch e.organizer " +
            "left join fetch em.bike " +
            "left join fetch m.likedNews ln " +
            "left join fetch ln.news " +
            "left join fetch m.bikes " +
            "left join fetch m.membershipFees " +
            "where m.id = :id")
    Optional<Member> findByIdCustom(long id);

    /**
     * Get a members given its email address, with required fetched relations.
     *
     * @return The optional member with the specified email address
     */
    @Query("select m from Member m " +
            "left join fetch m.eventMembers em " +
            "left join fetch em.event e " +
            "left join fetch e.participants " +
            "left join fetch e.organizer " +
            "left join fetch em.bike " +
            "left join fetch m.likedNews ln " +
            "left join fetch ln.news " +
            "left join fetch m.bikes " +
            "left join fetch m.membershipFees " +
            "where m.email = :email")
    Optional<Member> findByEmailCustom(String email);

    /**
     * Get all members from the database corresponding to the specified text filter, with required fetched relations.
     *
     * @return The list of all members corresponding to the specified text filter
     */
    @Query("select distinct m from Member m " +
            "left join fetch m.eventMembers em " +
            "left join fetch em.event e " +
            "left join fetch e.organizer " +
            "left join fetch m.likedNews ln " +
            "left join fetch ln.news " +
            "left join fetch m.bikes " +
            "left join fetch m.membershipFees " +
            "where :text is null or ( " +
            "m.firstName like %:text% " +
            "or m.lastName like %:text% " +
            "or m.email like %:text%" +
            ") " +
            "order by m.firstName, m.lastName")
    List<Member> findFilteredCustom(String text);

    /**
     * Fetch only the avatar attachment for the given member, with no Member graph load at all.
     *
     * @param memberId id of the member
     * @return the attachment, or empty when the member doesn't exist
     * or has no avatar
     */
    @Query("select m.avatar from Member m where m.id = :memberId")
    Optional<Attachment> findAvatarByMemberId(long memberId);

    /**
     * Check whether a member exists with the given email address.
     *
     * @param email The email to check
     * @return {@code true} when a member with this email exists, {@code false} otherwise
     */
    boolean existsMemberByEmail(String email);

    /**
     * Find all members currently holding the given board role, excluding the member with id {@code excludedId}
     * (typically the one being assigned the role).
     *
     * @return The list of members holding the given board role
     */
    List<Member> findByBoardRoleAndIdNot(BoardRole boardRole, Long excludedId);
}
