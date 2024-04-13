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

package com.ccteam.graphql.controller.graphql;

import com.ccteam.graphql.entities.Event;
import com.ccteam.graphql.entities.Member;
import com.ccteam.graphql.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * {@link Member} GraphQL controller.
 *
 * @author yann39
 * @since 1.0.0
 */
@Controller
@Slf4j
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * Get all members.
     *
     * @return A list of {@link Member} objects representing the members
     */
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @QueryMapping
    public List<Member> getAllMembers() {
        log.info("Received call to getAllMembers");
        return memberService.getAllMembers();
    }

    /**
     * Get a member given its {@code id}.
     *
     * @param id The ID of the member to retrieve
     * @return A {@link Member} object representing the member
     */
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @QueryMapping
    public Member getMemberById(@Argument Long id) {
        log.info("Received call to getMemberById with parameters ID = {}", id);
        return memberService.getMemberById(id);
    }

    /**
     * Get a member given its e-mail address.
     *
     * @param email The member's e-mail address
     * @return A {@link Member} object representing the member
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @QueryMapping
    public Member getMemberByEmail(@Argument String email) {
        log.info("Received call to getMemberByEmail with parameters email = {}", email);
        return memberService.getMemberByEmail(email);
    }

    /**
     * Get all members according to the specified filter {@code text}.<br/>
     * Search is done on first name, last name and e-mail address.<br/>
     * If {@code text} filter is null, all records will be returned.
     *
     * @param text The text filter string
     * @return A list of {@link Member} objects representing the members
     */
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @QueryMapping
    public List<Member> getMembersFiltered(@Argument String text) {
        log.info("Received call to getMembersFiltered with parameters text = {}", text);
        return memberService.getMembersFiltered(text);
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @MutationMapping
    public Member createMember(@Argument String firstName,
                               @Argument String lastName,
                               @Argument String email,
                               @Argument String phone,
                               @Argument String avatarUrl,
                               @Argument String bike,
                               @Argument boolean active,
                               @Argument boolean admin) {
        log.info("Received call to createMember with parameters firstName = {}, lastName = {}, email = {}, phone = {}, avatarUrl = {}, bike = {}, active = {}, admin = {}",
                firstName, lastName, email, phone, avatarUrl, bike, active, admin);
        return memberService.createMember(firstName, lastName, email, phone, avatarUrl, bike, active, admin);
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @MutationMapping
    public Member updateMember(@Argument long memberId,
                               @Argument String firstName,
                               @Argument String lastName,
                               @Argument String email,
                               @Argument String phone,
                               @Argument String avatarUrl,
                               @Argument String bike,
                               @Argument boolean active,
                               @Argument boolean admin) {
        log.info("Received call to updateMember with parameters memberId = {}, firstName = {}, lastName = {}, email = {}, phone = {}, avatarUrl = {}, bike = {}, active = {}, admin = {}",
                memberId, firstName, lastName, email, phone, avatarUrl, bike, active, admin);
        return memberService.updateMember(memberId, firstName, lastName, email, phone, avatarUrl, bike, active, admin);
    }

    /**
     * Delete the member represented by the given event ID.
     *
     * @param memberId The ID of the {@link Event} to delete
     * @return A {@link Event} object representing the member just deleted
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @MutationMapping
    public Member deleteMember(@Argument long memberId) {
        log.info("Received call to deleteMember with parameters memberId = {}", memberId);
        return memberService.deleteMember(memberId);
    }

}