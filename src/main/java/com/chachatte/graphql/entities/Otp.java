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

package com.chachatte.graphql.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author yann39
 * @since 1.0.0
 */
@Getter
@Setter
@Entity
@Table(name = "otp")
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 128, nullable = false)
    private String email;

    @Column
    private String otp;

    @Column
    private LocalDateTime otpDate;

    @Column
    private int attempts;

    @Column(nullable = false)
    private LocalDateTime createdOn = LocalDateTime.now();

}