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

import com.ccteam.graphql.entities.Circuit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * {@link Circuit} repository.
 *
 * @author yann39
 * @since 1.3.0
 */
@Repository
public interface CircuitRepository extends JpaRepository<Circuit, Long> {

    /**
     * Retrieve all circuits with their country and versions loaded, ordered by name.
     *
     * @return The list of circuits with associations fetched
     */
    @Query("select distinct c " +
            "from Circuit c " +
            "join fetch c.country " +
            "left join fetch c.tracks " +
            "order by c.name")
    List<Circuit> findAllCustom();

    /**
     * Find a circuit by id and fetch its country and versions eagerly.
     *
     * @param id The circuit id
     * @return The optional circuit with associations fetched
     */
    @Query("select c " +
            "from Circuit c " +
            "join fetch c.country " +
            "left join fetch c.tracks " +
            "where c.id = :id")
    Optional<Circuit> findByIdCustom(long id);

    /**
     * Find circuits filtered by text against the circuit name. When {@code text} is null, returns all circuits.
     * Country and versions are fetched eagerly.
     *
     * @param text The filter text (nullable)
     * @return The list of circuits matching the filter
     */
    @Query("select distinct c " +
            "from Circuit c " +
            "join fetch c.country " +
            "left join fetch c.tracks " +
            "where :text is null or ( " +
            "c.name like %:text%" +
            ") order by c.name")
    List<Circuit> findFilteredCustom(String text);

}
