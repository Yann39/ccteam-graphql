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

package com.chachatte.graphql.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Custom authentication entry point used to customize the response when an authentication exception is raised,
 * before sending the response back to the client.
 * <p>
 * If the user tries to authenticate with wrong credentials, an {@link AuthenticationException} is thrown.
 * Then {@link AuthenticationEntryPoint} is called, and the {@code commence()} method is triggered.
 * <p>
 * We use this because we raise a {@link BadCredentialsException} in our custom authentication manager
 * ({@link CustomDbAuthenticationManager}) which would make the server returns a <i>500 Internal Server Error</i>,
 * but we want to return a <i>401 Unauthorized</i>.
 *
 * @author yann39
 * @since 1.0.0
 */
@Component
@Slf4j
public class CustomDbAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException authException) {
        log.info("Calling CustomDbAuthenticationEntryPoint commence");
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}