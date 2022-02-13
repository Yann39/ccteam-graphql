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

package com.chachatte.graphql.graphql.query;

import com.chachatte.graphql.dto.MemberDto;
import com.chachatte.graphql.entities.Member;
import com.chachatte.graphql.repository.MemberRepository;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author yann39
 * @since sept 2020
 */
@Component
@Slf4j
public class MemberQueryResolver implements GraphQLQueryResolver {

    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    public MemberQueryResolver(MemberRepository memberRepository, ModelMapper modelMapper) {
        this.memberRepository = memberRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Get all members.
     *
     * @return A list of DTO objects representing the members
     */
    @Secured("ROLE_MEMBER")
    public Iterable<MemberDto> getAllMembers() {
        log.info("Received call to getAllMembers");
        final List<Member> result = new ArrayList<>();
        memberRepository.findAll().forEach(result::add);
        return result.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    /**
     * Get a member given the specified {@code id}.
     *
     * @param id The member ID
     * @return A list of DTO objects representing the members
     */
    @Secured("ROLE_MEMBER")
    public MemberDto getMemberById(Long id) {
        log.info("Received call to getMemberById with parameter ID = " + id);
        final Optional<Member> member = memberRepository.findById(id);
        return member.map(this::convertToDto).orElse(null);
    }

    /**
     * Get a member given its e-mail address.
     *
     * @param email The member's e-mail address
     * @return A list of DTO objects representing the members
     */
    //@Secured("ROLE_USER")
    public MemberDto getMemberByEmail(String email) {
        log.info("Received call to getMemberByEmail");
        final Optional<Member> member = memberRepository.findByEmail(email);
        return member.map(this::convertToDto).orElse(null);
    }

    /**
     * Get all members that have liked the news identified by the specified {@code newsId}.
     *
     * @param newsId The news ID
     * @return A list of DTO objects representing the members
     */
    @Secured("ROLE_MEMBER")
    public Iterable<MemberDto> getMembersLikedNews(long newsId) {
        log.info("Received call to getAllMembers");
        return memberRepository.findByNewsId(newsId).stream().map(this::convertToDto).collect(Collectors.toList());
    }

    /**
     * Get all members according to the specified filter {@code text}.<br/>
     * Search is done on first name, last name and e-mail address.<br/>
     * If {@code text} filter is null, all records will be returned.
     *
     * @param text The text filter
     * @return A list of DTO objects representing the members
     */
    @Secured("ROLE_MEMBER")
    public Iterable<MemberDto> getMembersFiltered(String text) {
        log.info("Received call to getMembersFiltered with parameter text = " + text);
        if (text != null && text.length() > 0) {
            return memberRepository.findByActiveTrueAndFirstNameLikeOrLastNameLikeOrEmailLike("%" + text + "%", "%" + text + "%", "%" + text + "%").stream().map(this::convertToDto).collect(Collectors.toList());
        } else {
            return memberRepository.findByActiveTrue().stream().map(this::convertToDto).collect(Collectors.toList());
        }
    }

    /**
     * Convert the specified {@link Member} entity object to a {@link MemberDto} DTO object.
     *
     * @param member The member to convert
     * @return The {@link MemberDto} object created
     */
    private MemberDto convertToDto(Member member) {
        return modelMapper.map(member, MemberDto.class);
    }
}