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

package com.chachatte.graphql.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author yann39
 * @since sept 2020
 */
@Getter
@Setter
@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 64, nullable = false)
    private String firstName;

    @Column(length = 64, nullable = false)
    private String lastName;

    @Column(length = 128, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 13)
    private String phone;

    @Column
    private String avatarUrl;

    @Column(length = 128)
    private String bike;

    @Column(nullable = false)
    private boolean active = false;

    @Column(nullable = false)
    private boolean admin = false;

    @Enumerated(EnumType.STRING)
    @Column
    private Role role;

    @Column(nullable = false)
    private LocalDateTime registrationDate;

    @Column(nullable = false)
    private LocalDateTime createdOn = LocalDateTime.now();

    @Column
    private LocalDateTime modifiedOn;

    public enum Role implements GrantedAuthority {
        ROLE_USER,
        ROLE_MEMBER,
        ROLE_ADMIN;

        @Override
        public String getAuthority() {
            return this.name();
        }
    }

}
