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

package com.chachatte.graphql.config.security;

import com.chachatte.graphql.entities.Member;
import com.chachatte.graphql.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Custom service to allow Spring Security to load user information from the database.
 * <p>
 * The {@link UserDetailsService} interface has a method to load User by username and returns a
 * {@link UserDetails} object that Spring Security can use for authentication and validation.
 *
 * @author yann39
 * @since oct 2020
 */
@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        log.info("Calling JPAUserDetailsService loadUserByUsername");
        final Optional<Member> user = memberRepository.findByEmail(userName);
        user.orElseThrow(() -> new UsernameNotFoundException("Not found: " + userName));
        return user.map(CustomUserDetails::new).get();
    }

}