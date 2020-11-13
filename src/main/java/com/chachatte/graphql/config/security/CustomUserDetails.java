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
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Custom service that provides user information, it acts as an adapter between the application user
 * and the representation of the user details needed by Spring Security.
 * <p>
 * It implements the {@link UserDetails} interface which basically holds principals, credentials, authorities
 * and other information that is regarding a particular user.
 *
 * @author yann39
 * @since oct 2020
 */

@Getter
@Setter
public class CustomUserDetails implements UserDetails {

    private static final long serialVersionUID = 275347623L;
    private long id;
    private String username;
    private String password;
    private boolean active;
    private List<GrantedAuthority> authorities;

    public CustomUserDetails(Member user) {
        this.id = user.getId();
        this.username = user.getEmail();
        this.password = user.getPassword();
        this.active = user.isActive();
        this.authorities = Arrays.stream(new String[]{"ROLE_MEMBER"}).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}