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

import com.ccteam.graphql.entities.Bike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * {@link Bike} repository.
 *
 * @author yann39
 * @since 1.0.0
 */
@Repository
public interface BikeRepository extends JpaRepository<Bike, Long> {

    /**
     * Find all bikes of the given member that are flagged as
     * {@code current}, excluding the bike with the given id.
     *
     * @param memberId    the owner member id
     * @param excludedId  the id of the bike to exclude (e.g. the one
     *                    being marked as current)
     * @return the list of other bikes of the member that still hold the
     *         current flag
     */
    List<Bike> findByMemberIdAndCurrentTrueAndIdNot(Long memberId, Long excludedId);

}
