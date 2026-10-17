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

package com.ccteam.graphql.repository;

import com.ccteam.graphql.entities.BikeEquipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * {@link BikeEquipment} repository.
 *
 * @author yann39
 * @since 1.2.4
 */
@Repository
public interface BikeEquipmentRepository extends JpaRepository<BikeEquipment, Long> {

    /**
     * Get the equipments of the given bike, in the order set by the member (entry order for equal positions).
     *
     * @param bikeId The bike id
     * @return The equipments of the bike
     */
    List<BikeEquipment> findByBikeIdOrderByPositionAscIdAsc(Long bikeId);

    /**
     * Delete all the equipments of the given bike, before the bike itself gets deleted.
     *
     * @param bikeId The bike id
     */
    @Transactional
    void deleteByBikeId(Long bikeId);

}
