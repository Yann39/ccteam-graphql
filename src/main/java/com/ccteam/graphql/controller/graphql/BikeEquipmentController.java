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

import com.ccteam.graphql.entities.BikeEquipment;
import com.ccteam.graphql.enums.CurrencyCode;
import com.ccteam.graphql.service.BikeEquipmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Bike equipment GraphQL controller.
 * <p>
 * Equipments are private: the bike is always checked against the authenticated member, so a member only ever sees
 * and edits the equipments of their own bikes. Every mutation returns the whole, fresh list of the bike.
 *
 * @author yann39
 * @since 1.2.4
 */
@Controller
@Slf4j
public class BikeEquipmentController {

    private final BikeEquipmentService bikeEquipmentService;

    public BikeEquipmentController(BikeEquipmentService bikeEquipmentService) {
        this.bikeEquipmentService = bikeEquipmentService;
    }

    /**
     * Get the equipments of one of the authenticated member's bikes.
     *
     * @param bikeId         The bike ID
     * @param authentication The current Spring Security authentication
     * @return The equipments of the bike
     */
    @PreAuthorize("hasRole('MEMBER')")
    @QueryMapping
    public List<BikeEquipment> getBikeEquipments(@Argument Long bikeId, Authentication authentication) {
        log.info("Received call to getBikeEquipments with parameters bikeId = {}", bikeId);
        return bikeEquipmentService.getBikeEquipments(bikeId, authentication.getName());
    }

    /**
     * Add an equipment to a bike.
     *
     * @param bikeId         The bike ID
     * @param designation    The name of the part
     * @param price          The price (optional)
     * @param currency       The currency of the price
     * @param comment        A free comment (optional)
     * @param authentication The current Spring Security authentication
     * @return The updated equipments of the bike
     */
    @PreAuthorize("hasRole('MEMBER')")
    @MutationMapping
    public List<BikeEquipment> createBikeEquipment(@Argument Long bikeId,
                                                   @Argument String designation,
                                                   @Argument Double price,
                                                   @Argument CurrencyCode currency,
                                                   @Argument String comment,
                                                   Authentication authentication) {
        log.info("Received call to createBikeEquipment with parameters bikeId = {}, designation = {}",
                bikeId, designation);
        return bikeEquipmentService.createBikeEquipment(bikeId, designation, price, currency, comment,
                authentication.getName());
    }

    /**
     * Update an existing equipment.
     *
     * @param equipmentId    The equipment ID
     * @param designation    The name of the part
     * @param price          The price (optional)
     * @param currency       The currency of the price
     * @param comment        A free comment (optional)
     * @param authentication The current Spring Security authentication
     * @return The updated equipments of the bike
     */
    @PreAuthorize("hasRole('MEMBER')")
    @MutationMapping
    public List<BikeEquipment> updateBikeEquipment(@Argument Long equipmentId,
                                                   @Argument String designation,
                                                   @Argument Double price,
                                                   @Argument CurrencyCode currency,
                                                   @Argument String comment,
                                                   Authentication authentication) {
        log.info("Received call to updateBikeEquipment with parameters equipmentId = {}, designation = {}",
                equipmentId, designation);
        return bikeEquipmentService.updateBikeEquipment(equipmentId, designation, price, currency, comment,
                authentication.getName());
    }

    /**
     * Delete an equipment.
     *
     * @param equipmentId    The equipment ID
     * @param authentication The current Spring Security authentication
     * @return The updated equipments of the bike
     */
    @PreAuthorize("hasRole('MEMBER')")
    @MutationMapping
    public List<BikeEquipment> deleteBikeEquipment(@Argument Long equipmentId, Authentication authentication) {
        log.info("Received call to deleteBikeEquipment with parameters equipmentId = {}", equipmentId);
        return bikeEquipmentService.deleteBikeEquipment(equipmentId, authentication.getName());
    }

    /**
     * Reorder the equipments of a bike.
     *
     * @param bikeId         The bike ID
     * @param equipmentIds   The IDs of the equipments of the bike, in their new order
     * @param authentication The current Spring Security authentication
     * @return The equipments of the bike, in their new order
     */
    @PreAuthorize("hasRole('MEMBER')")
    @MutationMapping
    public List<BikeEquipment> reorderBikeEquipments(@Argument Long bikeId,
                                                     @Argument List<Long> equipmentIds,
                                                     Authentication authentication) {
        log.info("Received call to reorderBikeEquipments with parameters bikeId = {}, equipmentIds = {}",
                bikeId, equipmentIds);
        return bikeEquipmentService.reorderBikeEquipments(bikeId, equipmentIds, authentication.getName());
    }
}
