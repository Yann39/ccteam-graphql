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
     * Retrieve all tracks with their country loaded, ordered by name.
     *
     * @return The list of tracks with country fetched
     */
    @Query("select t " +
            "from Track t " +
            "join fetch t.country " +
            "order by t.name")
    List<Track> findAllCustom();

    /**
     * Find a track by id and fetch its country eagerly.
     *
     * @param id The track id
     * @return The optional track with country fetched
     */
    @Query("select t " +
            "from Track t " +
            "join fetch t.country " +
            "where t.id = :id")
    Optional<Track> findByIdCustom(long id);

    /**
     * Find tracks filtered by text against the track name. When {@code text} is null, returns all tracks.
     * Country is fetched eagerly.
     *
     * @param text The filter text (nullable)
     * @return The list of tracks matching the filter
     */
    @Query("select t " +
            "from Track t " +
            "join fetch t.country " +
            "where :text is null or ( " +
            "t.name like %:text%" +
            ") order by t.name")
    List<Track> findFilteredCustom(String text);

}
