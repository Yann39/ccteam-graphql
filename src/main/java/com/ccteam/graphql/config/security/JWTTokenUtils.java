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

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ccteam.graphql.entities.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * JWT token utilities.
 *
 * @author yann39
 * @since 1.0.0
 */
@Component
@Slf4j
public class JWTTokenUtils {

    @Autowired
    private JWTTokenProperties jwtTokenProperties;

    /**
     * Decode the JWT token from the specified authorization header content.
     *
     * @param authorizationHeader The authorization header value as {@link String}
     * @return The {@link JWTTokenPayload} object containing the decoded JWT information
     */
    public JWTTokenPayload decodeToken(String authorizationHeader) {
        log.info("Calling JWTTokenUtils decodeToken");
        final DecodedJWT decodedToken = JWT.require(Algorithm.HMAC512(jwtTokenProperties.getSecret().getBytes())).build().verify(authorizationHeader.replace("Bearer ", ""));
        return new JWTTokenPayload(decodedToken.getSubject(), decodedToken.getClaim("role").as(Member.Role.class));
    }

    /**
     * Generate a new JWT token from the specified subject and role.
     *
     * @param subject The subject claim of the JWT payload data
     * @param role    The role to be set as claim value in the JWT
     * @return The new created JWT token as {@link String}
     */
    public String generateToken(String subject, String role) {
        log.info("Calling JWTTokenUtils generateToken");
        final LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Zurich"));
        final Instant instant = now.plusSeconds(jwtTokenProperties.getExpirationTime() / 1000).atZone(ZoneId.of("Europe/Zurich")).toInstant();

        log.info("Building JWT... it will expire on " + instant.atZone(ZoneId.of("Europe/Zurich")));

        return JWT.create()
                .withSubject(subject)
                .withClaim("role", role)
                .withExpiresAt(Date.from(instant))
                .sign(Algorithm.HMAC512(jwtTokenProperties.getSecret().getBytes()));
    }

}