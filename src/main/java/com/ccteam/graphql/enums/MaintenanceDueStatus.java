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

package com.ccteam.graphql.enums;

/**
 * Where a bike stands relative to its next maintenance, according to its maintenance plan.
 *
 * @author yann39
 * @since 1.3.0
 */
public enum MaintenanceDueStatus {

    /**
     * The due date can't be computed: no plan, or no maintenance recorded yet to count from.
     */
    UNKNOWN,

    /**
     * The next maintenance is not due soon.
     */
    OK,

    /**
     * The next maintenance is approaching (see the thresholds in the maintenance service).
     */
    DUE_SOON,

    /**
     * The next maintenance date or mileage has been exceeded.
     */
    OVERDUE

}
