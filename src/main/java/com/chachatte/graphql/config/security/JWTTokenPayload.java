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
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *  Represent JWT token payload data.
 *
 * @author yann39
 * @since oct 2020
 */
@AllArgsConstructor
@Getter
public class JWTTokenPayload {

    private final String email;
    private final Member.Role role;

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("email", email).append("role", role).toString();
    }
}