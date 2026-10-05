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
import java.util.ArrayList;
import java.util.List;

/**
 * A maintenance performed on a bike, made of one or more {@link MaintenanceOperation}s.
 *
 * @author yann39
 * @since 1.3.0
 */
@Getter
@Setter
@Entity
@Table(name = "maintenance")
public class Maintenance {

    /**
     * Database identifier (primary key) for this maintenance.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The serviced bike.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_id", nullable = false)
    private Bike bike;

    /**
     * Date of the maintenance.
     */
    @Column(nullable = false)
    private LocalDateTime maintenanceDate;

    /**
     * Odometer reading at the time of the maintenance, in kilometers (optional).
     */
    @Column(name = "odometer_km")
    private Integer odometerKm;

    /**
     * Whether this maintenance is a service of the {@link MaintenancePlan}, i.e. restarts the plan countdown.
     * A one-off repair (a tire, brake pads...) does not, the next service stays due from the previous one.
     * <p>
     * Defaults to {@code true} in the database so the maintenances recorded before the flag existed keep counting.
     */
    @Column(name = "resets_plan", nullable = false, columnDefinition = "boolean default true")
    private Boolean resetsPlan = true;

    /**
     * Free comment, maximum 1000 characters.
     */
    @Column(length = 1000)
    private String comment;

    /**
     * Operations performed during this maintenance, in the order they were entered.
     */
    @OneToMany(mappedBy = "maintenance", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position")
    private List<MaintenanceOperation> operations = new ArrayList<>();

    /**
     * Timestamp when the maintenance was created.
     */
    @Column(nullable = false)
    private LocalDateTime createdOn;

    /**
     * Timestamp when the maintenance was last modified.
     */
    @Column
    private LocalDateTime modifiedOn;

}
