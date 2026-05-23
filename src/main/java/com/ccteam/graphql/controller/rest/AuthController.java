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

package com.ccteam.graphql.controller.rest;

import com.ccteam.graphql.config.security.*;
import com.ccteam.graphql.entities.Member;
import com.ccteam.graphql.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * REST authentication controller.
 * <p>
 * Implements a per-account progressive lockout to mitigate brute-force attempts on the 6-digit passcode.
 * The thresholds and durations live in {@link LockoutPolicy} so the curve is documented and tweakable in
 * one place. The counter and {@code lockedUntil} timestamp are stored on the {@link Member} entity.
 *
 * @author yann39
 * @since 1.0.0
 */
@RestController
@CrossOrigin
@Component
@Slf4j
public class AuthController {

    private final JWTTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;
    private final MemberRepository memberRepository;

    public AuthController(JWTTokenUtils jwtTokenUtils,
                          AuthenticationManager authenticationManager,
                          MemberRepository memberRepository) {
        this.jwtTokenUtils = jwtTokenUtils;
        this.authenticationManager = authenticationManager;
        this.memberRepository = memberRepository;
    }

    /**
     * Authenticate the user according to the specified email and password.
     * <p>
     * Outcomes:
     * <ul>
     *   <li><b>423 Locked</b> : account is currently in a lockout window, body carries the number of seconds
     *       left before unlock.</li>
     *   <li><b>401 Unauthorized</b> : wrong passcode (or unknown email), body carries {@code attemptsLeft} when
     *       known so the client can warn the user before the next lockout kicks in.</li>
     *   <li><b>500 Internal Server Error</b> : unexpected auth failure (anonymous token, etc.).
     *       Should not happen under normal usage but kept as a safety net.</li>
     *   <li><b>200 Ok</b> : authentication succeeded, JWT in body.</li>
     * </ul>
     *
     * @param userRequest e-mail address + passcode
     */
    @Transactional
    @PostMapping("/rest/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody JWTRequest userRequest) {
        log.info("Call to authenticate REST endpoint");

        // check if the account is currently locked, we do this before the BCrypt comparison so a flood of
        // attempts can't keep the CPU busy hashing during a lockout
        final Optional<Member> memberOptional = memberRepository.findByEmailCustom(userRequest.getEmail());
        if (memberOptional.isPresent()) {
            final Member member = memberOptional.get();
            final LocalDateTime lockedUntil = member.getLockedUntil();
            if (lockedUntil != null && lockedUntil.isAfter(LocalDateTime.now())) {
                final long secondsLeft = Duration.between(LocalDateTime.now(), lockedUntil).getSeconds();
                log.info("Authentication refused for {}, account locked for {} more seconds",
                        userRequest.getEmail(), secondsLeft);
                return ResponseEntity.status(HttpStatus.LOCKED)
                        .body(AuthenticationErrorResponse.locked(Math.max(1L, secondsLeft)));
            }
        }

        // verify the passcode via Spring Security's AuthenticationManager
        final Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userRequest.getEmail(), userRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            if (memberOptional.isEmpty()) {
                log.info("Authentication refused for unknown email {}", userRequest.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            final Member member = memberOptional.get();
            final int newCount = member.getFailedLoginAttempts() + 1;
            member.setFailedLoginAttempts(newCount);

            // apply the progressive lockout policy
            final Duration lockoutDuration = LockoutPolicy.durationFor(newCount);
            if (!lockoutDuration.isZero()) {
                final LocalDateTime until = LocalDateTime.now().plus(lockoutDuration);
                member.setLockedUntil(until);
                memberRepository.save(member);
                log.info("Authentication failed for {}, {} consecutive failures, locked for {} seconds",
                        userRequest.getEmail(), newCount, lockoutDuration.getSeconds());
                return ResponseEntity.status(HttpStatus.LOCKED)
                        .body(AuthenticationErrorResponse.locked(Math.max(1L, lockoutDuration.getSeconds())));
            }

            memberRepository.save(member);
            final int attemptsLeft = LockoutPolicy.attemptsLeftBeforeLockout(newCount);
            log.info("Authentication failed for {}, {} consecutive failures, {} attempts left before lockout",
                    userRequest.getEmail(), newCount, attemptsLeft);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthenticationErrorResponse.badCredentials(attemptsLeft));
        }

        // success path
        if (!(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            final CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            if (customUserDetails == null) {
                log.info("Authentication refused for unknown email {}", userRequest.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            final String username = customUserDetails.getUsername();
            final String role = authentication.getAuthorities().iterator().next().toString();
            log.info("User {} authenticated with role {}", username, role);

            // reset the lockout counter so legitimate users always start fresh after a successful login
            if (memberOptional.isPresent()) {
                final Member member = memberOptional.get();
                if (member.getFailedLoginAttempts() != 0 || member.getLockedUntil() != null) {
                    member.setFailedLoginAttempts(0);
                    member.setLockedUntil(null);
                    memberRepository.save(member);
                }
            }

            final String jwtToken = jwtTokenUtils.generateToken(username, role);
            return ResponseEntity.ok(new JWTResponse(jwtToken));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
