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

package com.ccteam.graphql.service;

import com.ccteam.graphql.config.graphql.CustomGraphQLException;
import com.ccteam.graphql.entities.Circuit;
import com.ccteam.graphql.entities.Country;
import com.ccteam.graphql.repository.CircuitRepository;
import com.ccteam.graphql.repository.CountryRepository;
import com.ccteam.graphql.repository.TrackRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * {@link Circuit} service.
 *
 * @author yann39
 * @since 1.3.0
 */
@Service
@Slf4j
public class CircuitService {

    private final CircuitRepository circuitRepository;
    private final CountryRepository countryRepository;
    private final TrackRepository trackRepository;

    public CircuitService(CircuitRepository circuitRepository, CountryRepository countryRepository, TrackRepository trackRepository) {
        this.circuitRepository = circuitRepository;
        this.countryRepository = countryRepository;
        this.trackRepository = trackRepository;
    }

    /**
     * Resolve a {@link Country} from its ISO 3166-1 alpha-2 code. The country is mandatory on circuits,
     * so a {@code null} or blank code is rejected.
     *
     * @param countryCode The country code (mandatory)
     * @return The matching {@link Country}
     * @throws CustomGraphQLException If the code is missing or does not match any known country
     */
    private Country resolveCountry(String countryCode) {
        if (countryCode == null || countryCode.isBlank()) {
            log.error("Country code is required for circuit creation/update");
            throw new CustomGraphQLException("country_required", "A country code is required");
        }
        return countryRepository.findById(countryCode.toUpperCase())
                .orElseThrow(() -> {
                    log.error("Country with code {} not found", countryCode);
                    return new CustomGraphQLException("country_not_found", "Specified country code has not been found");
                });
    }

    /**
     * Get all circuits.
     *
     * @return A list of {@link Circuit} objects representing the circuits
     */
    public List<Circuit> getAllCircuits() {
        return circuitRepository.findAllCustom();
    }

    /**
     * Get all circuits according to the specified filter {@code text}.<br/>
     * Search is done on circuit name.<br/>
     * If {@code text} filter is null, all circuits will be returned.
     *
     * @param text The text filter string
     * @return A list of {@link Circuit} objects representing the circuits
     */
    public List<Circuit> getCircuitsFiltered(String text) {
        return circuitRepository.findFilteredCustom(text);
    }

    /**
     * Get a circuit given its {@code id}.
     *
     * @param id The ID of the circuit to retrieve
     * @return A {@link Circuit} object representing the circuit
     */
    public Circuit getCircuitById(Long id) {
        final Optional<Circuit> circuitOptional = circuitRepository.findByIdCustom(id);
        if (circuitOptional.isEmpty()) {
            log.error("Circuit with id {} not found in the database", id);
            throw new CustomGraphQLException("circuit_not_found", "Specified circuit has not been found in the database");
        }
        return circuitOptional.get();
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
    @Transactional
    public Circuit createCircuit(String name, String countryCode, BigDecimal latitude, BigDecimal longitude, String website) {
        final Circuit circuit = new Circuit();
        circuit.setName(name);
        circuit.setCountry(resolveCountry(countryCode));
        circuit.setLatitude(latitude);
        circuit.setLongitude(longitude);
        circuit.setWebsite(website);
        return circuitRepository.save(circuit);
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
    @Transactional
    public Circuit updateCircuit(long circuitId, String name, String countryCode, BigDecimal latitude, BigDecimal longitude, String website) {
        final Optional<Circuit> circuitOptional = circuitRepository.findByIdCustom(circuitId);
        if (circuitOptional.isEmpty()) {
            log.error("Circuit with id {} not found in the database", circuitId);
            throw new CustomGraphQLException("circuit_not_found", "Specified circuit ID has not been found in the database");
        }

        final Circuit circuit = circuitOptional.get();
        circuit.setName(name);
        circuit.setCountry(resolveCountry(countryCode));
        circuit.setLatitude(latitude);
        circuit.setLongitude(longitude);
        circuit.setWebsite(website);
        return circuitRepository.save(circuit);
    }

    /**
     * Delete the circuit represented by the given circuit ID.
     * <p>
     * A circuit that still has at least one version (track) cannot be deleted; every version must be
     * deleted first.
     *
     * @param circuitId The ID of the {@link Circuit} to delete
     * @return A {@link Circuit} object representing the circuit just deleted
     */
    @Transactional
    public Circuit deleteCircuit(long circuitId) {
        final Optional<Circuit> circuitOptional = circuitRepository.findByIdCustom(circuitId);
        if (circuitOptional.isEmpty()) {
            log.error("Circuit with id {} not found in the database", circuitId);
            throw new CustomGraphQLException("circuit_not_found", "Specified circuit ID has not been found in the database");
        }

        if (trackRepository.existsByCircuit_Id(circuitId)) {
            log.error("Circuit with id {} still has versions and cannot be deleted", circuitId);
            throw new CustomGraphQLException("circuit_has_versions", "Delete all versions before deleting the circuit");
        }

        final Circuit circuit = circuitOptional.get();
        circuitRepository.delete(circuit);
        return circuit;
    }

}
