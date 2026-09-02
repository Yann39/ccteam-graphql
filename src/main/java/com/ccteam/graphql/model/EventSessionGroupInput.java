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

/**
 * A group of identical sessions as entered by an admin, e.g. {@code 5 × 20 min}.
 * <p>
 * Groups are how humans describe a track day ("5 sessions of 20 minutes and 1 of 25"), they are expanded
 * server-side into one {@link com.ccteam.graphql.entities.EventSession} row per session so that participants
 * can tick individual sessions.
 *
 * @author yann39
 * @since 1.2.2
 */
public record EventSessionGroupInput(
        Integer count,
        Integer durationMinutes
) {
}
