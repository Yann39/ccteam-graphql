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

import com.ccteam.graphql.entities.EventMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * {@link EventMember} repository.
 *
 * @author yann39
 * @since 1.0.0
 */
@Repository
public interface EventMemberRepository extends JpaRepository<EventMember, Long> {

    /**
     * Null out the {@code bike} reference on every {@link EventMember} row pointing to the given bike id.
     * Called by {@code BikeService.deleteBike} to honor the "deleting a bike keeps your participation,
     * just clears the bike info" contract, we can't rely on a database-level {@code ON DELETE SET NULL}
     * because the FK was added via Hibernate auto-DDL without that clause, and adding it retroactively
     * would require a migration.
     *
     * @param bikeId ID of the bike being deleted
     * @return The number of rows updated
     */
    @Modifying
    @Query("update EventMember em set em.bike = null where em.bike.id = :bikeId")
    int clearBikeRef(long bikeId);

}
