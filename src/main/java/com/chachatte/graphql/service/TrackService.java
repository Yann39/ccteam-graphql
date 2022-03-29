/*
 * Copyright (c) 2022 by Yann39
 *
 * This file is part of Chachatte Team GraphQL application.
 *
 * Chachatte Team GraphQL is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Chachatte Team GraphQL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with Chachatte Team GraphQL. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.chachatte.graphql.service;

import com.chachatte.graphql.config.graphql.CustomGraphQLException;
import com.chachatte.graphql.entities.News;
import com.chachatte.graphql.entities.Track;
import com.chachatte.graphql.repository.TrackRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * {@link News} service.
 *
 * @author yann39
 * @since 1.0.0
 */
@Service
@Slf4j
public class TrackService {

    @Autowired
    private TrackRepository trackRepository;

    /**
     * Get all tracks.
     *
     * @return A list of {@link Track} objects representing the tracks
     */
    public List<Track> getAllTracks() {
        return trackRepository.findAllCustom();
    }

    /**
     * Get a track given its {@code id}.
     *
     * @param id The ID of the track to retrieve
     * @return A {@link Track} object representing the track
     */
    public Track getTrackById(Long id) {
        final Optional<Track> trackOptional = trackRepository.findByIdCustom(id);
        if (trackOptional.isEmpty()) {
            log.error("Track with id " + id + " not found in the database");
            throw new CustomGraphQLException("track_not_found", "Specified track has not been found in the database");
        }
        return trackOptional.get();
    }

    /**
     * Create a new track.
     *
     * @param name      The official name of the track
     * @param distance  The track distance (in meters)
     * @param lapRecord The lap record (in milliseconds)
     * @param website   The official website of the track
     * @param latitude  The track latitude coordinate
     * @param longitude The track longitude coordinate
     * @return A {@link Track} object representing the track just created
     */
    public Track createTrack(String name, int distance, int lapRecord, String website, BigDecimal latitude, BigDecimal longitude) {
        final Track track = new Track();
        track.setName(name);
        track.setDistance(distance);
        track.setLapRecord(lapRecord);
        track.setWebsite(website);
        track.setLatitude(latitude);
        track.setLongitude(longitude);
        return trackRepository.save(track);
    }

}