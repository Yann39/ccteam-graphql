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

package com.chachatte.graphql.graphql.mutation;

import com.chachatte.graphql.dto.MemberDto;
import com.chachatte.graphql.entities.Member;
import com.chachatte.graphql.graphql.exception.CustomGraphQLException;
import com.chachatte.graphql.repository.MemberRepository;
import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author yann39
 * @since sept 2020
 */
@Component
public class MemberMutationResolver implements GraphQLMutationResolver {

    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public MemberMutationResolver(MemberRepository memberRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Create a new member.
     *
     * @param firstName The member first name
     * @param lastName  The member last name
     * @param email     The member e-mail address
     * @param password  The member password
     * @return A DTO object representing the member just created
     */
    public MemberDto newMember(String firstName, String lastName, String email, String password) throws CustomGraphQLException {
        if (memberRepository.existsMemberByEmail(email)) {
            throw new CustomGraphQLException("user_already_exist", "Un utilisateur ayant la même adresse e-mail existe déjà");
        }
        final Member member = new Member();
        member.setFirstName(firstName);
        member.setLastName(lastName);
        member.setEmail(email);
        member.setPassword(passwordEncoder.encode(password));
        member.setRole(Member.Role.ROLE_USER);
        final Member created = memberRepository.save(member);
        return convertToDto(created);
    }

    /**
     * Convert the specified {@link Member} object to an {@link MemberDto} object
     *
     * @param member The member to convert
     * @return The {@link MemberDto} object created
     */
    private MemberDto convertToDto(Member member) {
        return modelMapper.map(member, MemberDto.class);
    }
}