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
import com.ccteam.graphql.entities.Member;
import com.ccteam.graphql.repository.BikeRepository;
import com.ccteam.graphql.repository.EventMemberRepository;
import com.ccteam.graphql.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * {@link Bike} service.
 *
 * @author yann39
 * @since 1.0.0
 */
@Service
@Slf4j
public class BikeService {

    private final BikeRepository bikeRepository;
    private final MemberRepository memberRepository;
    private final EventMemberRepository eventMemberRepository;

    public BikeService(BikeRepository bikeRepository,
                       MemberRepository memberRepository,
                       EventMemberRepository eventMemberRepository) {
        this.bikeRepository = bikeRepository;
        this.memberRepository = memberRepository;
        this.eventMemberRepository = eventMemberRepository;
    }

    /**
     * Add a bike to a member.
     *
     * @param memberId     The ID of the member
     * @param manufacturer The manufacturer
     * @param modelName    The model name
     * @param engineSize   The engine size
     * @param year         The year
     * @return The created {@link Bike}
     */
    @Transactional
    public Bike addBikeToMember(Long memberId, String manufacturer, String modelName, Integer engineSize,
            Integer year) {
        final Optional<Member> memberOptional = memberRepository.findByIdCustom(memberId);
        if (memberOptional.isEmpty()) {
            log.error("Member with id {} not found", memberId);
            throw new CustomGraphQLException("member_not_found", "Specified member has not been found");
        }

        final Bike bike = new Bike();
        bike.setManufacturer(manufacturer);
        bike.setModelName(modelName);
        bike.setEngineSize(engineSize);
        bike.setYear(year);
        bike.setMember(memberOptional.get());

        return bikeRepository.save(bike);
    }

    /**
     * Update an existing bike.
     *
     * <p>If {@code current} is set to {@code true}, any other bike of the
     * same member that was previously flagged as current will be unflagged
     * so that the "one current bike per member" invariant is preserved.</p>
     *
     * @param bikeId       The ID of the bike to update
     * @param manufacturer The manufacturer
     * @param modelName    The model name
     * @param engineSize   The engine size
     * @param year         The year
     * @param current      Whether this bike should become the member's
     *                     current bike (may be {@code null}, in which case
     *                     the existing flag is preserved)
     * @return The updated {@link Bike}
     */
    @Transactional
    public Bike updateBike(Long bikeId, String manufacturer, String modelName, Integer engineSize, Integer year,
            Boolean current) {
        final Optional<Bike> bikeOptional = bikeRepository.findById(bikeId);
        if (bikeOptional.isEmpty()) {
            log.error("Bike with id {} not found", bikeId);
            throw new CustomGraphQLException("bike_not_found", "Specified bike has not been found");
        }

        final Bike bike = bikeOptional.get();
        bike.setManufacturer(manufacturer);
        bike.setModelName(modelName);
        bike.setEngineSize(engineSize);
        bike.setYear(year);

        if (current != null) {
            // when marking this bike as current, unflag any other bike of
            // the same member that was previously flagged as current
            if (current && bike.getMember() != null) {
                final List<Bike> previousCurrents = bikeRepository
                        .findByMemberIdAndCurrentTrueAndIdNot(bike.getMember().getId(), bike.getId());
                for (Bike previous : previousCurrents) {
                    previous.setCurrent(false);
                    bikeRepository.save(previous);
                }
            }
            bike.setCurrent(current);
        }

        return bikeRepository.save(bike);
    }

    /**
     * Delete a bike.
     * <p>
     * Any {@link com.ccteam.graphql.entities.EventMember} participation
     * that had this bike pinned gets its bike reference cleared first.
     *
     * @param bikeId The ID of the bike to delete
     * @return The deleted {@link Bike}
     */
    @Transactional
    public Bike deleteBike(Long bikeId) {
        final Optional<Bike> bikeOptional = bikeRepository.findById(bikeId);
        if (bikeOptional.isEmpty()) {
            log.error("Bike with id {} not found", bikeId);
            throw new CustomGraphQLException("bike_not_found", "Specified bike has not been found");
        }

        final int clearedParticipations = eventMemberRepository.clearBikeRef(bikeId);
        if (clearedParticipations > 0) {
            log.info("Cleared bike id {} from {} event participation(s) before deletion", bikeId, clearedParticipations);
        }

        final Bike bike = bikeOptional.get();
        bikeRepository.delete(bike);
        return bike;
    }
}
