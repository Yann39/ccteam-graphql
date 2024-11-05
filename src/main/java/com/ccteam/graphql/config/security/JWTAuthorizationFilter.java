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

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

/**
 * Custom security filter to be added to the filter chain to process JWT authentication.
 * <p>
 * It extends {@link BasicAuthenticationFilter} which normally processes an HTTP request's basic authentication,
 * but here we override the internal filter to get authentication information from the <i>Authorization</i> header
 * instead, which contain the JWT token, and then put the result into the {@link SecurityContextHolder}.
 *
 * @author yann39
 * @since 1.0.0
 */
@Slf4j
@Component
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private final JWTTokenUtils jwtTokenUtils;

    public JWTAuthorizationFilter(AuthenticationManager authManager, JWTTokenUtils jwtTokenUtils) {
        super(authManager);
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        log.info("Calling JWTAuthorizationFilter doFilterInternal with URL = {}", req.getRequestURL());

        // retrieve request authorization header
        final String authorizationHeader = req.getHeader("Authorization");

        // authorization header must be set and start with Bearer
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

            try {

                // decode JWT token
                final JWTTokenPayload jwtTokenPayload = jwtTokenUtils.decodeToken(authorizationHeader);

                // if user e-mail has been retrieved correctly from the token
                if (jwtTokenPayload.getEmail() != null) {

                    // if user is not already authenticated
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        // authenticate user
                        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(jwtTokenPayload.getEmail(), null, Collections.singletonList(jwtTokenPayload.getRole()));

                        // set authentication in security context holder
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        log.debug("User is already authenticated");
                    }

                } else {
                    log.error("Valid token contains no user info");
                }

            } catch (TokenExpiredException e) {
                log.info("Token expired : {}", e.getMessage());
                req.setAttribute("token_expired", e.getMessage());
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } catch (BadCredentialsException e) {
                log.info("Bad credentials in token : {}", e.getMessage());
                req.setAttribute("bad_credentials", e.getMessage());
                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            } catch (JWTDecodeException e) {
                log.info("Problem when decoding token : {}", e.getMessage());
                req.setAttribute("wrong_token_format", e.getMessage());
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
        // no token specified
        else {
            log.info("Request does not contain authorization header with Bearer");
            req.setAttribute("no_token", "Request does not contain authorization header with Bearer");
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        // pass request down the chain, except for OPTIONS requests
        if (!"OPTIONS".equalsIgnoreCase(req.getMethod())) {
            chain.doFilter(req, res);
        }

    }

}