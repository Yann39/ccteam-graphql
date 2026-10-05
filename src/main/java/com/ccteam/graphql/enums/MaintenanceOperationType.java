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
 * Catalog of the operations that can be performed during a bike maintenance.
 * <p>
 * Stored by name, the mobile application holds the localized labels. {@link #OTHER} lets the member describe an
 * operation that is not in the catalog with a free label.
 *
 * @author yann39
 * @since 1.3.0
 */
public enum MaintenanceOperationType {

    ENGINE_OIL,
    OIL_FILTER,
    AIR_FILTER,
    SPARK_PLUGS,
    COOLANT,
    BRAKE_PADS_FRONT,
    BRAKE_PADS_REAR,
    BRAKE_DISCS,
    BRAKE_FLUID,
    TIRE_FRONT,
    TIRE_REAR,
    CHAIN_KIT,
    FORK_SERVICE,
    SHOCK_SERVICE,
    VALVE_CLEARANCE,
    BATTERY,
    GENERAL_INSPECTION,
    OTHER

}
