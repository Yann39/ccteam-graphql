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

package com.chachatte.graphql.controller.graphql;

import com.chachatte.graphql.entities.Track;
import com.chachatte.graphql.service.TrackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.List;

/**
 * {@link Track} GraphQL controller.
 *
 * @author yann39
 * @since 1.0.0
 */
@Controller
@Slf4j
public class TrackController {

    private final TrackService trackService;

    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }

    /**
     * Get all tracks.
     *
     * @return A list of {@link Track} objects representing the tracks
     */
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @QueryMapping
    public List<Track> getAllTracks() {
        log.info("Received call to getAllTracks");
        return trackService.getAllTracks();
    }

    /**
     * Get a track given its {@code id}.
     *
     * @param id The ID of the track to retrieve
     * @return A {@link Track} object representing the track
     */
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @QueryMapping
    public Track getTrackById(@Argument Long id) {
        log.info("Received call to getTrackById with parameter ID = {}", id);
        return trackService.getTrackById(id);
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @MutationMapping
    public Track createTrack(@Argument String name,
                             @Argument int distance,
                             @Argument int lapRecord,
                             @Argument String website,
                             @Argument BigDecimal latitude,
                             @Argument BigDecimal longitude) {
        log.info("Received call to createTrack with parameters name = {}, distance = {}, lapRecord = {}, website = {}, latitude = {}, longitude = {}",
                name, distance, lapRecord, website, latitude, longitude);
        return trackService.createTrack(name, distance, lapRecord, website, latitude, longitude);
    }

    /**
     * Update the track represented by the given track ID with the specified data.
     *
     * @param trackId   The ID of the {@link Track} to update
     * @param name      The official name of the track
     * @param distance  The track distance (in meters)
     * @param lapRecord The lap record (in milliseconds)
     * @param website   The official website of the track
     * @param latitude  The track latitude coordinate
     * @param longitude The track longitude coordinate
     * @return A {@link Track} object representing the track just updated
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @MutationMapping
    public Track updateTrack(@Argument long trackId,
                             @Argument String name,
                             @Argument int distance,
                             @Argument int lapRecord,
                             @Argument String website,
                             @Argument BigDecimal latitude,
                             @Argument BigDecimal longitude) {
        log.info("Received call to updateTrack with parameters trackId = {}, name = {}, distance = {}, lapRecord = {}, website = {}, latitude = {}, longitude = {}",
                trackId, name, distance, lapRecord, website, latitude, longitude);
        return trackService.updateTrack(trackId, name, distance, lapRecord, website, latitude, longitude);
    }

    /**
     * Delete the track represented by the given track ID.
     *
     * @param trackId The ID of the {@link Track} to delete
     * @return A {@link Track} object representing the track just deleted
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @MutationMapping
    public Track deleteTrack(@Argument long trackId) {
        log.info("Received call to deleteTrack with parameter trackId = {}", trackId);
        return trackService.deleteTrack(trackId);
    }

}