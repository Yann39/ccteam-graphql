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

import com.ccteam.graphql.entities.Bike;
import com.ccteam.graphql.service.BikeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

/**
 * {@link Bike} GraphQL controller.
 *
 * @author yann39
 * @since 1.0.0
 */
@Controller
@Slf4j
public class BikeController {

    private final BikeService bikeService;

    public BikeController(BikeService bikeService) {
        this.bikeService = bikeService;
    }

    /**
     * Add a bike to a member.
     *
     * @param memberId     The member ID
     * @param manufacturer The manufacturer
     * @param modelName    The model name
     * @param engineSize   The engine size
     * @param year         The year
     * @return The created {@link Bike}
     */
    @PreAuthorize("hasRole('MEMBER')")
    @MutationMapping
    public Bike addBikeToMember(@Argument Long memberId,
            @Argument String manufacturer,
            @Argument String modelName,
            @Argument Integer engineSize,
            @Argument Integer year) {
        log.info("Received call to addBikeToMember with parameters memberId = {}, manufacturer = {}, modelName = {}",
                memberId, manufacturer, modelName);
        return bikeService.addBikeToMember(memberId, manufacturer, modelName, engineSize, year);
    }

    /**
     * Update an existing bike.
     *
     * @param bikeId       The bike ID
     * @param manufacturer The manufacturer
     * @param modelName    The model name
     * @param engineSize   The engine size
     * @param year         The year
     * @param current      Whether this bike is the member's current bike
     *                     (optional). When set to {@code true} the service
     *                     unflags any other bike of the same member.
     * @return The updated {@link Bike}
     */
    @PreAuthorize("hasRole('MEMBER')")
    @MutationMapping
    public Bike updateBike(@Argument Long bikeId,
            @Argument String manufacturer,
            @Argument String modelName,
            @Argument Integer engineSize,
            @Argument Integer year,
            @Argument Boolean current) {
        log.info("Received call to updateBike with parameters bikeId = {}, manufacturer = {}, modelName = {}, current = {}",
                bikeId, manufacturer, modelName, current);
        return bikeService.updateBike(bikeId, manufacturer, modelName, engineSize, year, current);
    }

    /**
     * Delete an existing bike.
     *
     * @param bikeId The bike ID
     * @return The deleted {@link Bike}
     */
    @PreAuthorize("hasRole('MEMBER')")
    @MutationMapping
    public Bike deleteBike(@Argument Long bikeId) {
        log.info("Received call to deleteBike with parameters bikeId = {}", bikeId);
        return bikeService.deleteBike(bikeId);
    }
}
