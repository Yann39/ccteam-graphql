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

package com.ccteam.graphql.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Maintenance plan of a bike: the bike must be serviced every {@code intervalKm} kilometers or every
 * {@code intervalMonths} months, whichever comes first. At least one of the two intervals is set.
 * <p>
 * A bike has at most one plan.
 *
 * @author yann39
 * @since 1.3.0
 */
@Getter
@Setter
@Entity
@Table(name = "maintenance_plan")
public class MaintenancePlan {

    /**
     * Database identifier (primary key) for this plan.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The bike this plan applies to.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_id", nullable = false, unique = true)
    private Bike bike;

    /**
     * Distance between two maintenances, in kilometers ({@code null} when the plan is time-based only).
     */
    @Column(name = "interval_km")
    private Integer intervalKm;

    /**
     * Time between two maintenances, in months ({@code null} when the plan is distance-based only).
     */
    @Column(name = "interval_months")
    private Integer intervalMonths;

    /**
     * Timestamp when the plan was created.
     */
    @Column(nullable = false)
    private LocalDateTime createdOn;

    /**
     * Timestamp when the plan was last modified.
     */
    @Column
    private LocalDateTime modifiedOn;

}
