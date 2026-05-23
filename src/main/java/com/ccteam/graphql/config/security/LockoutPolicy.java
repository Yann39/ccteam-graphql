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

import java.time.Duration;

/**
 * Progressive lockout policy for failed login attempts.
 * <p>
 * Single source of truth for the rate-limiting curve so the controller and any future caller
 * (Flutter UI sanity-checks, admin tooling, tests) agree on the numbers.
 * <p>
 * Mapping (consecutive failed attempts ⇒ lockout duration after the failure is recorded):
 * <ul>
 *   <li>1, 2          → no lockout (regular bad-credentials response)</li>
 *   <li>3, 4          → 1 minute</li>
 *   <li>5, 6          → 5 minutes</li>
 *   <li>7, 8, 9       → 15 minutes</li>
 *   <li>10 and beyond → 1 hour (capped)</li>
 * </ul>
 * The counter is reset to 0 on any successful authentication.
 *
 * @author yann39
 * @since 1.0.0
 */
public final class LockoutPolicy {

    /**
     * Number of free attempts before the first lockout kicks in.
     */
    public static final int FREE_ATTEMPTS = 3;

    private LockoutPolicy() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Lockout duration after recording {@code failedAttempts} consecutive failures.
     * {@code Duration.ZERO} means "no lockout, just a regular bad-credentials response".
     *
     * @return The lockout duration
     */
    public static Duration durationFor(int failedAttempts) {
        if (failedAttempts < 3) return Duration.ZERO;
        if (failedAttempts < 5) return Duration.ofMinutes(1);
        if (failedAttempts < 7) return Duration.ofMinutes(5);
        if (failedAttempts < 10) return Duration.ofMinutes(15);
        return Duration.ofHours(1);
    }

    /**
     * Number of attempts the caller has left before the next lockout kicks in, given {@code failedAttempts}
     * already-recorded failures.
     *
     * @return 0 once the next attempt would itself trigger a lockout (i.e. once we're already at or past
     * {@link #FREE_ATTEMPTS}).
     */
    public static int attemptsLeftBeforeLockout(int failedAttempts) {
        final int left = FREE_ATTEMPTS - failedAttempts;
        return Math.max(0, left);
    }
}
