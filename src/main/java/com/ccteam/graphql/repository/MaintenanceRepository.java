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

import com.ccteam.graphql.entities.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * {@link Maintenance} repository.
 *
 * @author yann39
 * @since 1.3.0
 */
@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {

    /**
     * Get all the maintenances of the given bike with their operations, most recent first.
     *
     * @param bikeId The bike id
     * @return The maintenances of the bike
     */
    @Query("select distinct m from Maintenance m " +
            "left join fetch m.operations " +
            "where m.bike.id = :bikeId " +
            "order by m.maintenanceDate desc, m.id desc")
    List<Maintenance> findByBikeIdWithOperations(Long bikeId);

    /**
     * Get the most recent service of the plan of the given bike, the maintenance the next one is due from.
     *
     * @param bikeId The bike id
     * @return The last service, empty when none was recorded
     */
    Optional<Maintenance> findFirstByBikeIdAndResetsPlanTrueOrderByMaintenanceDateDescIdDesc(Long bikeId);

    /**
     * Get all the maintenances of the given bike, used to delete them along with the bike.
     *
     * @param bikeId The bike id
     * @return The maintenances of the bike
     */
    List<Maintenance> findByBikeId(Long bikeId);

}
