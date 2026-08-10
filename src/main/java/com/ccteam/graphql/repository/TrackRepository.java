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

import com.ccteam.graphql.entities.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * {@link Track} repository.
 *
 * @author yann39
 * @since 1.0.0
 */
@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {

    /**
     * Retrieve all tracks (versions) with their circuit and country loaded, ordered by circuit
     * name then version label.
     *
     * @return The list of tracks with circuit fetched
     */
    @Query("select t " +
           "from Track t " +
           "left join fetch t.circuit c " +
           "left join fetch c.country " +
           "order by c.name, t.variantName")
    List<Track> findAllCustom();

    /**
     * Find a track by id and fetch its circuit (and country) eagerly.
     *
     * @param id The track id
     * @return The optional track with circuit fetched
     */
    @Query("select t " +
           "from Track t " +
           "left join fetch t.circuit c " +
           "left join fetch c.country " +
           "where t.id = :id")
    Optional<Track> findByIdCustom(long id);

    /**
     * Whether at least one track (version) is attached to the given circuit. Used to block the
     * deletion of a circuit that still has versions.
     *
     * @param circuitId The circuit id
     * @return {@code true} when the circuit still has at least one version
     */
    boolean existsByCircuit_Id(Long circuitId);

}
