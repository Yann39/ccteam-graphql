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

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Custom authentication entry point used to customize the response on authentication exception before sending the
 * response back to the client.
 * <p>
 * If the user requests a secure resource without being authenticated, an {@link AuthenticationException} is thrown.<br/>
 * Then {@link AuthenticationEntryPoint} is called, and the {@code commence()} method is triggered.
 *
 * @author yann39
 * @since aug 2020
 */
@Component
@Slf4j
public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException authException) throws IOException {
        log.info("Calling JWTAuthenticationEntryPoint commence");
        res.setContentType("application/json;charset=UTF-8");
        final String expired = (String) req.getAttribute("token_expired");
        final String wrongTokenFormat = (String) req.getAttribute("wrong_token_format");
        final String noToken = (String) req.getAttribute("no_token");
        final String badCredentials = (String) req.getAttribute("bad_credentials");
        if (expired != null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("{\"originalMessage\":\"" + authException.getMessage() + "\", \"customCode\":\"token_expired\"}");
            log.info("return token_expired");
        } else if (wrongTokenFormat != null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("{\"originalMessage\":\"" + authException.getMessage() + "\", \"customCode\":\"wrong_token_format\"}");
            log.info("return wrong_token_format");
        } else if (noToken != null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("{\"originalMessage\":\"" + authException.getMessage() + "\", \"customCode\":\"no_token\"}");
            log.info("return no_token");
        } else if (badCredentials != null) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            res.getWriter().write("{\"originalMessage\":\"" + authException.getMessage() + "\", \"customCode\":\"bad_credentials\"}");
            log.info("return bad_credentials");
        } else {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write("{\"originalMessage\":\"" + authException.getMessage() + "\", \"customCode\":\"internal_error\"}");
            log.info("return internal_error");
        }
    }
}