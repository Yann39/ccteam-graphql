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

package com.ccteam.graphql.controller.graphql;

import com.ccteam.graphql.model.BikeMaintenanceView;
import com.ccteam.graphql.model.MaintenanceOperationInput;
import com.ccteam.graphql.service.MaintenanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Bike maintenance GraphQL controller.
 * <p>
 * Maintenance data is private: the bike is always checked against the authenticated member, so a member only ever
 * sees and edits the maintenance of their own bikes.
 *
 * @author yann39
 * @since 1.3.0
 */
@Controller
@Slf4j
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    /**
     * Get the maintenance plan, maintenances and next due maintenance of one of the authenticated member's bikes.
     *
     * @param bikeId         The bike ID
     * @param authentication The current Spring Security authentication
     * @return The maintenance data of the bike
     */
    @PreAuthorize("hasRole('MEMBER')")
    @QueryMapping
    public BikeMaintenanceView getBikeMaintenance(@Argument Long bikeId, Authentication authentication) {
        log.info("Received call to getBikeMaintenance with parameters bikeId = {}", bikeId);
        return maintenanceService.getBikeMaintenance(bikeId, authentication.getName());
    }

    /**
     * Define or replace the maintenance plan of a bike.
     *
     * @param bikeId         The bike ID
     * @param intervalKm     Distance between two maintenances, in kilometers (optional)
     * @param intervalMonths Time between two maintenances, in months (optional)
     * @param authentication The current Spring Security authentication
     * @return The updated maintenance data of the bike
     */
    @PreAuthorize("hasRole('MEMBER')")
    @MutationMapping
    public BikeMaintenanceView setMaintenancePlan(@Argument Long bikeId,
                                                  @Argument Integer intervalKm,
                                                  @Argument Integer intervalMonths,
                                                  Authentication authentication) {
        log.info("Received call to setMaintenancePlan with parameters bikeId = {}, intervalKm = {}, intervalMonths = {}",
                bikeId, intervalKm, intervalMonths);
        return maintenanceService.setMaintenancePlan(bikeId, intervalKm, intervalMonths, authentication.getName());
    }

    /**
     * Delete the maintenance plan of a bike.
     *
     * @param bikeId         The bike ID
     * @param authentication The current Spring Security authentication
     * @return The updated maintenance data of the bike
     */
    @PreAuthorize("hasRole('MEMBER')")
    @MutationMapping
    public BikeMaintenanceView deleteMaintenancePlan(@Argument Long bikeId, Authentication authentication) {
        log.info("Received call to deleteMaintenancePlan with parameters bikeId = {}", bikeId);
        return maintenanceService.deleteMaintenancePlan(bikeId, authentication.getName());
    }

    /**
     * Record a new maintenance.
     *
     * @param bikeId          The bike ID
     * @param maintenanceDate The maintenance date as ISO 8601 string
     * @param odometerKm      The odometer reading (optional)
     * @param resetsPlan      Whether the maintenance restarts the plan countdown (optional, defaults to yes)
     * @param comment         A free comment (optional)
     * @param operations      The operations performed
     * @param authentication  The current Spring Security authentication
     * @return The updated maintenance data of the bike
     */
    @PreAuthorize("hasRole('MEMBER')")
    @MutationMapping
    public BikeMaintenanceView createMaintenance(@Argument Long bikeId,
                                                 @Argument String maintenanceDate,
                                                 @Argument Integer odometerKm,
                                                 @Argument Boolean resetsPlan,
                                                 @Argument String comment,
                                                 @Argument List<MaintenanceOperationInput> operations,
                                                 Authentication authentication) {
        log.info("Received call to createMaintenance with parameters bikeId = {}, maintenanceDate = {}, odometerKm = {}",
                bikeId, maintenanceDate, odometerKm);
        return maintenanceService.createMaintenance(bikeId, maintenanceDate, odometerKm, resetsPlan, comment,
                operations, authentication.getName());
    }

    /**
     * Update an existing maintenance.
     *
     * @param maintenanceId   The maintenance ID
     * @param maintenanceDate The maintenance date as ISO 8601 string
     * @param odometerKm      The odometer reading (optional)
     * @param resetsPlan      Whether the maintenance restarts the plan countdown (optional, defaults to yes)
     * @param comment         A free comment (optional)
     * @param operations      The operations performed, replacing the previous ones
     * @param authentication  The current Spring Security authentication
     * @return The updated maintenance data of the bike
     */
    @PreAuthorize("hasRole('MEMBER')")
    @MutationMapping
    public BikeMaintenanceView updateMaintenance(@Argument Long maintenanceId,
                                                 @Argument String maintenanceDate,
                                                 @Argument Integer odometerKm,
                                                 @Argument Boolean resetsPlan,
                                                 @Argument String comment,
                                                 @Argument List<MaintenanceOperationInput> operations,
                                                 Authentication authentication) {
        log.info("Received call to updateMaintenance with parameters maintenanceId = {}, maintenanceDate = {}, odometerKm = {}",
                maintenanceId, maintenanceDate, odometerKm);
        return maintenanceService.updateMaintenance(maintenanceId, maintenanceDate, odometerKm, resetsPlan,
                comment, operations, authentication.getName());
    }

    /**
     * Delete a maintenance.
     *
     * @param maintenanceId  The maintenance ID
     * @param authentication The current Spring Security authentication
     * @return The updated maintenance data of the bike
     */
    @PreAuthorize("hasRole('MEMBER')")
    @MutationMapping
    public BikeMaintenanceView deleteMaintenance(@Argument Long maintenanceId, Authentication authentication) {
        log.info("Received call to deleteMaintenance with parameters maintenanceId = {}", maintenanceId);
        return maintenanceService.deleteMaintenance(maintenanceId, authentication.getName());
    }

    /**
     * Set the current odometer reading of a bike.
     *
     * @param bikeId         The bike ID
     * @param odometerKm     The odometer reading, in kilometers
     * @param authentication The current Spring Security authentication
     * @return The updated maintenance data of the bike
     */
    @PreAuthorize("hasRole('MEMBER')")
    @MutationMapping
    public BikeMaintenanceView updateBikeOdometer(@Argument Long bikeId, @Argument Integer odometerKm,
                                                  Authentication authentication) {
        log.info("Received call to updateBikeOdometer with parameters bikeId = {}, odometerKm = {}", bikeId, odometerKm);
        return maintenanceService.updateBikeOdometer(bikeId, odometerKm, authentication.getName());
    }
}
