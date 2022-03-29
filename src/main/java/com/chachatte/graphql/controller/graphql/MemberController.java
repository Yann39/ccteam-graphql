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

package com.chachatte.graphql.controller.graphql;

import com.chachatte.graphql.entities.Member;
import com.chachatte.graphql.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.annotation.Secured;
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
    @Secured("ROLE_MEMBER")
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
    @Secured("ROLE_MEMBER")
    @QueryMapping
    public Member getMemberById(@Argument Long id) {
        log.info("Received call to getMemberById with parameter ID = " + id);
        return memberService.getMemberById(id);
    }

    /**
     * Get a member given its e-mail address.
     *
     * @param email The member's e-mail address
     * @return A {@link Member} object representing the member
     */
    @Secured("ROLE_USER")
    @QueryMapping
    public Member getMemberByEmail(@Argument String email) {
        log.info("Received call to getMemberByEmail with parameter email = " + email);
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
    @Secured("ROLE_MEMBER")
    @QueryMapping
    public List<Member> getMembersFiltered(@Argument String text) {
        log.info("Received call to getMembersFiltered with parameter text = " + text);
        return memberService.getMembersFiltered(text);
    }

}