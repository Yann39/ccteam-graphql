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
import com.ccteam.graphql.entities.Event;
import com.ccteam.graphql.entities.EventMember;
import com.ccteam.graphql.entities.Member;
import com.ccteam.graphql.enums.BoardRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
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
            ") " +
            "order by m.firstName, m.lastName")
    List<Member> findFilteredCustom(String text);

    /**
     * Get a member given its ID, with required fetched relations (see {@link #fetchRelations(Member)}).
     *
     * @param id The member ID
     * @return The optional member with the specified ID
     */
    @Transactional(readOnly = true)
    default Optional<Member> findByIdCustom(long id) {
        final Optional<Member> member = findWithParticipationsById(id);
        member.ifPresent(this::fetchRelations);
        return member;
    }

    /**
     * Get a member given its email address, with required fetched relations (see {@link #fetchRelations(Member)}).
     *
     * @param email The member email address
     * @return The optional member with the specified email address
     */
    @Transactional(readOnly = true)
    default Optional<Member> findByEmailCustom(String email) {
        final Optional<Member> member = findWithParticipationsByEmail(email);
        member.ifPresent(this::fetchRelations);
        return member;
    }

    /**
     * Load the relations of the given member, on top of its participations, that its GraphQL projections read: the
     * participants and sessions of its events, its skipped sessions, liked news, bikes and membership fees.
     * <p>
     * Join fetching all of them in a single query would multiply their sizes (participations × participants ×
     * sessions × bikes × fees × liked news rows), one query per collection only adds them up. The queries run in the
     * transaction of the calling method, so each one completes the entities already loaded and the member is fully
     * initialized once detached. The avatar is left out: its bytes are served by the avatar endpoint, and
     * {@code hasAvatar} only needs the foreign key held by the member row.
     *
     * @param member The member, loaded with its participations in the current transaction
     */
    private void fetchRelations(Member member) {
        if (!member.getEventMembers().isEmpty()) {
            final List<Event> events = member.getEventMembers().stream().map(EventMember::getEvent).distinct().toList();
            fetchParticipants(events);
            fetchSessions(events);
            fetchSkippedSessions(member.getEventMembers());
        }
        fetchLikedNews(member);
        fetchBikes(member);
        fetchMembershipFees(member);
    }

    /**
     * Get a member given its ID with its participations, only joining single-valued associations past them so the
     * query returns one row per participation. Use {@link #findByIdCustom(long)} to get the other relations too.
     *
     * @param id The member ID
     * @return The optional member with the specified ID
     */
    @Query("select m from Member m " +
            "left join fetch m.eventMembers em " +
            "left join fetch em.event e " +
            "left join fetch e.track t " +
            "left join fetch t.circuit c " +
            "left join fetch c.country " +
            "left join fetch e.organizer " +
            "left join fetch em.bike " +
            "where m.id = :id")
    Optional<Member> findWithParticipationsById(long id);

    /**
     * Get a member given its email address with its participations, see {@link #findWithParticipationsById(long)}.
     * Use {@link #findByEmailCustom(String)} to get the other relations too.
     *
     * @param email The member email address
     * @return The optional member with the specified email address
     */
    @Query("select m from Member m " +
            "left join fetch m.eventMembers em " +
            "left join fetch em.event e " +
            "left join fetch e.track t " +
            "left join fetch t.circuit c " +
            "left join fetch c.country " +
            "left join fetch e.organizer " +
            "left join fetch em.bike " +
            "where m.email = :email")
    Optional<Member> findWithParticipationsByEmail(String email);

    /**
     * Load the participants of the given events, see {@link #fetchRelations(Member)}.
     *
     * @param events The events, already loaded in the current transaction
     * @return The same events
     */
    @Query("select e from Event e left join fetch e.participants where e in :events")
    List<Event> fetchParticipants(Collection<Event> events);

    /**
     * Load the sessions of the given events, see {@link #fetchRelations(Member)}.
     *
     * @param events The events, already loaded in the current transaction
     * @return The same events
     */
    @Query("select e from Event e left join fetch e.sessions where e in :events")
    List<Event> fetchSessions(Collection<Event> events);

    /**
     * Load the skipped sessions of the given participations, see {@link #fetchRelations(Member)}.
     *
     * @param eventMembers The participations, already loaded in the current transaction
     * @return The same participations
     */
    @Query("select em from EventMember em left join fetch em.skippedSessions where em in :eventMembers")
    List<EventMember> fetchSkippedSessions(Collection<EventMember> eventMembers);

    /**
     * Load the liked news of the given member, see {@link #fetchRelations(Member)}.
     *
     * @param member The member, already loaded in the current transaction
     * @return The same member
     */
    @Query("select m from Member m left join fetch m.likedNews ln left join fetch ln.news where m = :member")
    Member fetchLikedNews(Member member);

    /**
     * Load the bikes of the given member, see {@link #fetchRelations(Member)}.
     *
     * @param member The member, already loaded in the current transaction
     * @return The same member
     */
    @Query("select m from Member m left join fetch m.bikes where m = :member")
    Member fetchBikes(Member member);

    /**
     * Load the membership fees of the given member, see {@link #fetchRelations(Member)}.
     *
     * @param member The member, already loaded in the current transaction
     * @return The same member
     */
    @Query("select m from Member m left join fetch m.membershipFees where m = :member")
    Member fetchMembershipFees(Member member);

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

    /**
     * Count the club members, i.e. every member whose role differs from the given one.
     * Called with {@code ROLE_USER} to exclude the accounts still pending admin approval
     * (and the store-review test account) from the home-page members stat.
     *
     * @param role The role to exclude from the count
     * @return The number of members whose role differs from {@code role}
     */
    long countByRoleNot(Member.Role role);
}
