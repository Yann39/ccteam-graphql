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

package com.ccteam.graphql.config.security;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

/**
 * Body returned by {@code /rest/authenticate} when authentication fails with either
 * bad credentials (HTTP 401) or because the account is currently locked (HTTP 423).
 * <p>
 * Both fields are nullable and only the relevant one is populated:
 * <ul>
 *   <li>{@code attemptsLeft}, set on 401 responses: number of attempts the user has left before
 *       the next lockout kicks in.</li>
 *   <li>{@code lockedUntilSeconds}, set on 423 responses: number of seconds remaining before the account unlocks.
 *       Sent as a relative value (rather than an absolute timestamp) so the client doesn't have to worry about clock
 *       skew between server and device.</li>
 * </ul>
 *
 * @author yann39
 * @since 1.0.0
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenticationErrorResponse {

    private Integer attemptsLeft;
    private Long lockedUntilSeconds;

    public AuthenticationErrorResponse() {
    }

    public static AuthenticationErrorResponse badCredentials(int attemptsLeft) {
        final AuthenticationErrorResponse r = new AuthenticationErrorResponse();
        r.attemptsLeft = attemptsLeft;
        return r;
    }

    public static AuthenticationErrorResponse locked(long lockedUntilSeconds) {
        final AuthenticationErrorResponse r = new AuthenticationErrorResponse();
        r.lockedUntilSeconds = lockedUntilSeconds;
        return r;
    }
}
