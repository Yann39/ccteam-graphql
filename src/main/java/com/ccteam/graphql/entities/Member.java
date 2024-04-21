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

package com.ccteam.graphql.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author yann39
 * @since 1.0.0
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

    @Column(length = 128, nullable = false, unique = true)
    private String email;

    @Column
    private String password;

    @Column(length = 13)
    private String phone;

    @Column
    private String avatarUrl;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "attachment_id")
    private Attachment avatar;

    @Column(length = 128)
    private String bike;

    @Column(nullable = false)
    private boolean active = false;

    @Column(nullable = false)
    private boolean verified = false;

    @Column(nullable = false)
    private boolean admin = false;

    @Column
    private String otp;

    @Column
    private LocalDateTime otpDate;

    @Enumerated(EnumType.STRING)
    @Column
    private Role role;

    @Column
    private LocalDateTime registrationDate;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LikedNews> likedNews;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EventMember> eventMembers;

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
