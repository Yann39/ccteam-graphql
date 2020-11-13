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

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.chachatte.graphql.entities.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * JWT token utilities.
 *
 * @author yann39
 * @since oct 2020
 */
@Component
@Slf4j
public class JWTTokenUtils {

    @Autowired
    private JWTTokenProperties jwtTokenProperties;

    public JWTTokenPayload decodeToken(String authorizationHeader) {
        log.info("Calling JWTTokenUtils decodeToken");
        final DecodedJWT decodedToken = JWT.require(Algorithm.HMAC512(jwtTokenProperties.getSecret().getBytes())).build().verify(authorizationHeader.replace("Bearer ", ""));
        return new JWTTokenPayload(decodedToken.getSubject(), decodedToken.getClaim("role").as(Member.Role.class));
    }

    public String generateToken(Object id, Object role) {
        log.info("Calling JWTTokenUtils generateToken");
        final LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Zurich"));
        final Instant instant = now.plusSeconds(jwtTokenProperties.getExpirationTime() / 1000 + 3600).atZone(ZoneId.of("Europe/Zurich")).toInstant();

        log.info("Building JWT... it will expire on " + instant.atZone(ZoneId.of("Europe/Zurich")).toString());

        return JWT.create()
                .withSubject(id.toString())
                .withClaim("role", role.toString())
                .withExpiresAt(Date.from(instant))
                .sign(Algorithm.HMAC512(jwtTokenProperties.getSecret().getBytes()));
    }

    public String extractUsername(String token) {
        return decodeToken(token).getEmail();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        log.info("Calling JWTTokenUtils validateToken");
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()));
    }

}