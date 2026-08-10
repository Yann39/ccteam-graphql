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

import com.ccteam.graphql.entities.Circuit;
import com.ccteam.graphql.service.CircuitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.List;

/**
 * {@link Circuit} GraphQL controller.
 *
 * @author yann39
 * @since 1.3.0
 */
@Controller
@Slf4j
public class CircuitController {

    private final CircuitService circuitService;

    public CircuitController(CircuitService circuitService) {
        this.circuitService = circuitService;
    }

    /**
     * Get all circuits.
     *
     * @return A list of {@link Circuit} objects representing the circuits
     */
    @PreAuthorize("hasRole('USER')")
    @QueryMapping
    public List<Circuit> getAllCircuits() {
        log.info("Received call to getAllCircuits");
        return circuitService.getAllCircuits();
    }

    /**
     * Get all circuits according to the specified filter {@code text}.
     * <p>
     * Search is done on circuit name. If {@code text} filter is null, all circuits will be returned.
     *
     * @param text The text filter string
     * @return A list of {@link Circuit} objects representing the circuits
     */
    @PreAuthorize("hasRole('USER')")
    @QueryMapping
    public List<Circuit> getCircuitsFiltered(@Argument String text) {
        log.info("Received call to getCircuitsFiltered with parameter text = {}", text);
        return circuitService.getCircuitsFiltered(text);
    }

    /**
     * Get a circuit given its {@code id}.
     *
     * @param id The ID of the circuit to retrieve
     * @return A {@link Circuit} object representing the circuit
     */
    @PreAuthorize("hasRole('USER')")
    @QueryMapping
    public Circuit getCircuitById(@Argument Long id) {
        log.info("Received call to getCircuitById with parameter ID = {}", id);
        return circuitService.getCircuitById(id);
    }

    /**
     * Create a new circuit.
     *
     * @param name        The circuit (venue) name
     * @param countryCode ISO 3166-1 alpha-2 code of the country where the circuit is located (mandatory)
     * @param latitude    The circuit latitude coordinate
     * @param longitude   The circuit longitude coordinate
     * @param website     The circuit official website (optional)
     * @return A {@link Circuit} object representing the circuit just created
     */
    @PreAuthorize("hasRole('ADMIN')")
    @MutationMapping
    public Circuit createCircuit(@Argument String name,
                                 @Argument String countryCode,
                                 @Argument BigDecimal latitude,
                                 @Argument BigDecimal longitude,
                                 @Argument String website) {
        log.info("Received call to createCircuit with parameters name = {}, countryCode = {}, latitude = {}, longitude = {}, website = {}",
                name, countryCode, latitude, longitude, website);
        return circuitService.createCircuit(name, countryCode, latitude, longitude, website);
    }

    /**
     * Update the circuit represented by the given circuit ID with the specified data.
     *
     * @param circuitId   The ID of the {@link Circuit} to update
     * @param name        The circuit (venue) name
     * @param countryCode ISO 3166-1 alpha-2 code of the country where the circuit is located (mandatory)
     * @param latitude    The circuit latitude coordinate
     * @param longitude   The circuit longitude coordinate
     * @param website     The circuit official website (optional)
     * @return A {@link Circuit} object representing the circuit just updated
     */
    @PreAuthorize("hasRole('ADMIN')")
    @MutationMapping
    public Circuit updateCircuit(@Argument long circuitId,
                                 @Argument String name,
                                 @Argument String countryCode,
                                 @Argument BigDecimal latitude,
                                 @Argument BigDecimal longitude,
                                 @Argument String website) {
        log.info("Received call to updateCircuit with parameters circuitId = {}, name = {}, countryCode = {}, latitude = {}, longitude = {}, website = {}",
                circuitId, name, countryCode, latitude, longitude, website);
        return circuitService.updateCircuit(circuitId, name, countryCode, latitude, longitude, website);
    }

    /**
     * Delete the circuit represented by the given circuit ID.
     * <p>
     * The deletion is blocked when the circuit still has versions; all versions must be deleted first.
     *
     * @param circuitId The ID of the {@link Circuit} to delete
     * @return A {@link Circuit} object representing the circuit just deleted
     */
    @PreAuthorize("hasRole('ADMIN')")
    @MutationMapping
    public Circuit deleteCircuit(@Argument long circuitId) {
        log.info("Received call to deleteCircuit with parameter circuitId = {}", circuitId);
        return circuitService.deleteCircuit(circuitId);
    }

}
