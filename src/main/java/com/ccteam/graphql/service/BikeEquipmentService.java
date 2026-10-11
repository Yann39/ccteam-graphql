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
import com.ccteam.graphql.entities.Bike;
import com.ccteam.graphql.entities.BikeEquipment;
import com.ccteam.graphql.enums.CurrencyCode;
import com.ccteam.graphql.repository.BikeEquipmentRepository;
import com.ccteam.graphql.repository.BikeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Bike equipment service: the parts fitted to a bike (racing exhaust, crash pads, ...).
 * <p>
 * Like the maintenance data, the equipments are private: every operation checks that the bike belongs to the
 * authenticated member.
 *
 * @author yann39
 * @since 1.2.4
 */
@Service
@Slf4j
public class BikeEquipmentService {

    /**
     * Maximum length of the designation, the size of the database column.
     */
    static final int DESIGNATION_MAX_LENGTH = 128;

    /**
     * Maximum length of the comment, the size of the database column.
     */
    static final int COMMENT_MAX_LENGTH = 500;

    private final BikeRepository bikeRepository;
    private final BikeEquipmentRepository bikeEquipmentRepository;

    public BikeEquipmentService(BikeRepository bikeRepository, BikeEquipmentRepository bikeEquipmentRepository) {
        this.bikeRepository = bikeRepository;
        this.bikeEquipmentRepository = bikeEquipmentRepository;
    }

    /**
     * Get the equipments of the given bike.
     *
     * @param bikeId The bike id
     * @param email  The e-mail of the authenticated member, who must own the bike
     * @return The equipments of the bike, in the order they were entered
     */
    @Transactional(readOnly = true)
    public List<BikeEquipment> getBikeEquipments(Long bikeId, String email) {
        getOwnedBike(bikeId, email);
        return bikeEquipmentRepository.findByBikeIdOrderByIdAsc(bikeId);
    }

    /**
     * Add an equipment to the given bike.
     *
     * @param bikeId      The bike id
     * @param designation The name of the part
     * @param price       The price (optional)
     * @param currency    The currency of the price ({@code null} means CHF)
     * @param comment     A free comment (optional)
     * @param email       The e-mail of the authenticated member, who must own the bike
     * @return The updated equipments of the bike
     */
    @Transactional
    public List<BikeEquipment> createBikeEquipment(Long bikeId, String designation, Double price,
                                                   CurrencyCode currency, String comment, String email) {
        final Bike bike = getOwnedBike(bikeId, email);

        final BikeEquipment equipment = new BikeEquipment();
        equipment.setBike(bike);
        equipment.setCreatedOn(LocalDateTime.now());
        fillEquipment(equipment, designation, price, currency, comment);
        bikeEquipmentRepository.save(equipment);

        return bikeEquipmentRepository.findByBikeIdOrderByIdAsc(bikeId);
    }

    /**
     * Update an existing equipment.
     *
     * @param equipmentId The equipment id
     * @param designation The name of the part
     * @param price       The price (optional)
     * @param currency    The currency of the price ({@code null} means CHF)
     * @param comment     A free comment (optional)
     * @param email       The e-mail of the authenticated member, who must own the bike
     * @return The updated equipments of the bike
     */
    @Transactional
    public List<BikeEquipment> updateBikeEquipment(Long equipmentId, String designation, Double price,
                                                   CurrencyCode currency, String comment, String email) {
        final BikeEquipment equipment = getOwnedEquipment(equipmentId, email);

        fillEquipment(equipment, designation, price, currency, comment);
        equipment.setModifiedOn(LocalDateTime.now());
        bikeEquipmentRepository.save(equipment);

        return bikeEquipmentRepository.findByBikeIdOrderByIdAsc(equipment.getBike().getId());
    }

    /**
     * Delete an equipment.
     *
     * @param equipmentId The equipment id
     * @param email       The e-mail of the authenticated member, who must own the bike
     * @return The updated equipments of the bike
     */
    @Transactional
    public List<BikeEquipment> deleteBikeEquipment(Long equipmentId, String email) {
        final BikeEquipment equipment = getOwnedEquipment(equipmentId, email);
        final Long bikeId = equipment.getBike().getId();
        bikeEquipmentRepository.delete(equipment);
        bikeEquipmentRepository.flush();
        return bikeEquipmentRepository.findByBikeIdOrderByIdAsc(bikeId);
    }

    /**
     * Delete all the equipments of a bike, before the bike itself gets deleted.
     *
     * @param bikeId The bike id
     */
    @Transactional
    public void deleteBikeEquipments(Long bikeId) {
        bikeEquipmentRepository.deleteByBikeId(bikeId);
    }

    private void fillEquipment(BikeEquipment equipment, String designation, Double price, CurrencyCode currency,
                               String comment) {
        if (designation == null || designation.isBlank()) {
            throw new CustomGraphQLException("bike_equipment_designation_required",
                    "An equipment needs a designation");
        }
        final String trimmedDesignation = designation.trim();
        if (trimmedDesignation.length() > DESIGNATION_MAX_LENGTH) {
            throw new CustomGraphQLException("bike_equipment_designation_too_long",
                    "The designation of an equipment is limited to " + DESIGNATION_MAX_LENGTH + " characters");
        }
        final String trimmedComment = comment == null || comment.isBlank() ? null : comment.trim();
        if (trimmedComment != null && trimmedComment.length() > COMMENT_MAX_LENGTH) {
            throw new CustomGraphQLException("bike_equipment_comment_too_long",
                    "The comment of an equipment is limited to " + COMMENT_MAX_LENGTH + " characters");
        }
        if (price != null && price < 0) {
            throw new CustomGraphQLException("price_invalid", "The price must be a positive number");
        }

        equipment.setDesignation(trimmedDesignation);
        equipment.setPrice(price != null ? BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP) : null);
        equipment.setCurrency(currency != null ? currency : CurrencyCode.CHF);
        equipment.setComment(trimmedComment);
    }

    private Bike getOwnedBike(Long bikeId, String email) {
        final Bike bike = bikeRepository.findById(bikeId).orElseThrow(() -> {
            log.error("Bike with id {} not found", bikeId);
            return new CustomGraphQLException("bike_not_found", "Specified bike has not been found");
        });
        checkOwnership(bike, email);
        return bike;
    }

    private BikeEquipment getOwnedEquipment(Long equipmentId, String email) {
        final BikeEquipment equipment = bikeEquipmentRepository.findById(equipmentId).orElseThrow(() -> {
            log.error("Bike equipment with id {} not found", equipmentId);
            return new CustomGraphQLException("bike_equipment_not_found", "Specified equipment has not been found");
        });
        checkOwnership(equipment.getBike(), email);
        return equipment;
    }

    private static void checkOwnership(Bike bike, String email) {
        if (bike.getMember() == null || !bike.getMember().getEmail().equalsIgnoreCase(email)) {
            log.warn("Member {} tried to access the equipments of bike {} which is not theirs", email, bike.getId());
            throw new CustomGraphQLException("bike_not_owned", "Bike equipments are only available to the bike owner");
        }
    }

}
