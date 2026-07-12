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

package com.ccteam.graphql.model;

import java.time.LocalDateTime;

/**
 * Read-only view of a trusted device, exposed over GraphQL. Deliberately
 * omits the token hash (never leaves the server) and adds a {@code current}
 * flag telling whether it is the device making the request.
 *
 * @author yann39
 * @since 1.0.3
 */
public record TrustedDeviceView(
        Long id,
        String label,
        LocalDateTime createdOn,
        LocalDateTime lastUsedOn,
        boolean current
) {
}
