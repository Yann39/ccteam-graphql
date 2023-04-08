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

package com.chachatte.graphql.service;

import com.chachatte.graphql.config.graphql.CustomGraphQLException;
import com.chachatte.graphql.entities.Event;
import com.chachatte.graphql.entities.Member;
import com.chachatte.graphql.entities.News;
import com.chachatte.graphql.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * {@link News} service.
 *
 * @author yann39
 * @since 1.0.0
 */
@Service
@Slf4j
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    /**
     * Get all members.
     *
     * @return A list of {@link Member} objects representing the members
     */
    public List<Member> getAllMembers() {
        return memberRepository.findAllCustom();
    }

    /**
     * Get a member given its {@code id}.
     *
     * @param id The ID of the member to retrieve
     * @return A {@link Member} object representing the member
     */
    public Member getMemberById(Long id) {
        final Optional<Member> memberOptional = memberRepository.findByIdCustom(id);
        if (memberOptional.isEmpty()) {
            log.error("Member with id {} not found in the database", id);
            throw new CustomGraphQLException("member_not_found", "Specified member has not been found in the database");
        }
        return memberOptional.get();
    }

    /**
     * Get a member given its e-mail address.
     *
     * @param email The member's e-mail address
     * @return A {@link Member} object representing the member
     */
    public Member getMemberByEmail(String email) {
        final Optional<Member> memberOptional = memberRepository.findByEmailCustom(email);
        if (memberOptional.isEmpty()) {
            log.error("Member with e-mail {} not found in the database", email);
            throw new CustomGraphQLException("member_not_found", "Specified member has not been found in the database");
        }
        return memberOptional.get();
    }

    /**
     * Get all members according to the specified filter {@code text}.<br>
     * Search is done on first name, last name and e-mail address.<br>
     * If {@code text} filter is null, all records will be returned.
     *
     * @param text The text filter string
     * @return A list of {@link Member} objects representing the members
     */
    public List<Member> getMembersFiltered(String text) {
        return memberRepository.findFilteredCustom(text);
    }


    /**
     * Create a new member.
     *
     * @param firstName The member first name
     * @param lastName  The member last name
     * @param email     The member e-mail address
     * @param phone     The member phone number
     * @param avatarUrl The member avatar URL
     * @param bike      The member bike model
     * @param active    A boolean indicating if the member is active
     * @param admin     A boolean indicating if the member is admin
     * @return A {@link Member} object representing the member just created
     */
    public Member createMember(String firstName, String lastName, String email, String phone, String avatarUrl, String bike, boolean active, boolean admin) {

        final Optional<Member> memberOptional = memberRepository.findByEmailCustom(email);
        if (memberOptional.isPresent()) {
            log.error("Member with e-mail address {} already exist in the database", email);
            throw new CustomGraphQLException("member_email_already_exist", "A member with the same e-mail address already exist in the database");
        }

        final Member member = new Member();
        member.setFirstName(firstName);
        member.setLastName(lastName);
        member.setEmail(email);
        member.setPhone(phone);
        member.setAvatarUrl(avatarUrl);
        member.setBike(bike);
        member.setActive(active);
        member.setAdmin(admin);
        member.setRegistrationDate(LocalDateTime.now());
        member.setCreatedOn(LocalDateTime.now());
        return memberRepository.save(member);
    }

    /**
     * Update the member represented by the given member ID with the specified data.
     *
     * @param memberId  The ID of the {@link Member} to update
     * @param firstName The member first name
     * @param lastName  The member last name
     * @param email     The member e-mail address
     * @param phone     The member phone number
     * @param avatarUrl The member avatar URL
     * @param bike      The member bike model
     * @param active    A boolean indicating if the member is active
     * @param admin     A boolean indicating if the member is admin
     * @return An {@link Event} object representing the event just updated
     */
    public Member updateMember(long memberId, String firstName, String lastName, String email, String phone, String avatarUrl, String bike, boolean active, boolean admin) {
        final Optional<Member> memberOptional = memberRepository.findByIdCustom(memberId);
        if (memberOptional.isEmpty()) {
            log.error("Member with id {} not found in the database", memberId);
            throw new CustomGraphQLException("member_not_found", "Specified member ID has not been found in the database");
        }

        final Member member = memberOptional.get();
        member.setFirstName(firstName);
        member.setLastName(lastName);
        member.setEmail(email);
        member.setPhone(phone);
        member.setAvatarUrl(avatarUrl);
        member.setBike(bike);
        member.setActive(active);
        member.setAdmin(admin);
        member.setModifiedOn(LocalDateTime.now());
        return memberRepository.save(member);
    }

    /**
     * Delete the member represented by the given member ID.
     *
     * @param memberId The ID of the {@link Member} to delete
     * @return A {@link Member} object representing the member just deleted
     */
    public Member deleteMember(long memberId) {
        final Optional<Member> memberOptional = memberRepository.findByIdCustom(memberId);
        if (memberOptional.isEmpty()) {
            log.error("Member with id {} not found in the database", memberId);
            throw new CustomGraphQLException("member_not_found", "Specified member ID has not been found in the database");
        }

        final Member member = memberOptional.get();
        memberRepository.delete(member);
        return member;
    }

}