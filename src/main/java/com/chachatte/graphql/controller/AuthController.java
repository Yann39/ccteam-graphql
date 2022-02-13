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

package com.chachatte.graphql.controller;

import com.chachatte.graphql.config.security.JWTRequest;
import com.chachatte.graphql.config.security.JWTResponse;
import com.chachatte.graphql.config.security.JWTTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

/**
 * REST authentication controller.
 *
 * @author yann39
 * @since Oct 2020
 */
@RestController
@CrossOrigin
@Component
@Slf4j
public class AuthController {

    @Autowired
    private JWTTokenUtils jwtTokenUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Authenticate the user according to the the specified email and password.
     *
     * @param userRequest The request data containing the user's e-mail address and password
     * @return 403 Forbidden if wrong credentials have been specified<br/>
     * 500 Internal Server Error if any error have occurred during authentication<br/>
     * 200 Ok if authentication succeeded, with the issued JWT token in the response body
     */
    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> authenticate(@RequestBody JWTRequest userRequest) {

        // try to authenticate user using specified credentials
        final Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userRequest.getEmail(), userRequest.getPassword()));

        // if authentication succeeded and is not anonymous
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated()) {

            // set authentication in security context holder
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // get authorities, we should have only one role per member so simply get the first one
            final GrantedAuthority grantedAuthority = authentication.getAuthorities().iterator().next();

            // generate new JWT token
            final String jwtToken = jwtTokenUtils.generateToken(authentication.getPrincipal(), grantedAuthority);

            // return response containing the JWT token
            return ResponseEntity.ok(new JWTResponse(jwtToken));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    }

}