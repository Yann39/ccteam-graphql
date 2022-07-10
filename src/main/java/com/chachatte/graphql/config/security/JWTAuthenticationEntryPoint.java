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

import com.chachatte.graphql.config.graphql.CustomGraphQLException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Custom authentication entry point used to customize the response when an authentication exception is raised
 * before sending the response back to the client.
 * <p>
 * If the user requests a secure resource without being authenticated, an {@link AuthenticationException} is thrown.
 * Then {@link AuthenticationEntryPoint} is called, and the {@code commence()} method is triggered.
 * <p>
 * We use this to customize the server response when a problem occurs while verifying the JWT token in our {@link JWTAuthorizationFilter},
 * so that we can return a response in a GraphQL standard response format needed on Flutter side.
 *
 * @author yann39
 * @since 1.0.0
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

        // use custom GraphQL exception to set the error, so that it is in a standard GraphQL response format
        CustomGraphQLException customGraphQLException;

        if (expired != null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            customGraphQLException = new CustomGraphQLException("token_expired", authException.getMessage());
            log.info("return token_expired");
        } else if (wrongTokenFormat != null) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            customGraphQLException = new CustomGraphQLException("wrong_token_format", authException.getMessage());
            log.info("return wrong_token_format");
        } else if (noToken != null) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            customGraphQLException = new CustomGraphQLException("no_token", authException.getMessage());
            log.info("return no_token");
        } else if (badCredentials != null) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            customGraphQLException = new CustomGraphQLException("bad_credentials", authException.getMessage());
            log.info("return bad_credentials");
        } else {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            customGraphQLException = new CustomGraphQLException("internal_error", authException.getMessage());
            log.info("return internal_error");
        }

        // serialize GraphQL exception as JSON
        final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        final String json = ow.writeValueAsString(customGraphQLException);

        // write JSON to response and wrap it with "errors" so that the response is in a standard GraphQL response format
        res.getWriter().write("{ \"errors\": [ " + json + "], \"data\" : null }");
    }
}