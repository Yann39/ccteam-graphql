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

package com.ccteam.graphql.controller.graphql;

import com.ccteam.graphql.entities.Track;
import com.ccteam.graphql.service.TrackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * {@link Track} GraphQL controller. A track is a single version (layout) of a circuit.
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
     * Get all tracks (versions).
     *
     * @return A list of {@link Track} objects representing the tracks
     */
    @PreAuthorize("hasRole('USER')")
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
    @PreAuthorize("hasRole('USER')")
    @QueryMapping
    public Track getTrackById(@Argument Long id) {
        log.info("Received call to getTrackById with parameter ID = {}", id);
        return trackService.getTrackById(id);
    }

    /**
     * Create a new track (version) attached to a circuit.
     *
     * @param circuitId     The ID of the parent circuit (mandatory)
     * @param variantName   The version / layout label (optional, e.g. "5,8 km GP")
     * @param distance      The track distance (in meters)
     * @param lapRecord     The lap record (in milliseconds)
     * @param lapRecordInfo Additional information about the lap record (rider, bike, year, ...), optional
     * @param iconKey       Key selecting the version's map/shape icon, optional
     * @return A {@link Track} object representing the track just created
     */
    @PreAuthorize("hasRole('ADMIN')")
    @MutationMapping
    public Track createTrack(@Argument Long circuitId,
                             @Argument String variantName,
                             @Argument int distance,
                             @Argument int lapRecord,
                             @Argument String lapRecordInfo,
                             @Argument String iconKey) {
        log.info("Received call to createTrack with parameters circuitId = {}, variantName = {}, distance = {}, lapRecord = {}, lapRecordInfo = {}, iconKey = {}",
                circuitId, variantName, distance, lapRecord, lapRecordInfo, iconKey);
        return trackService.createTrack(circuitId, variantName, distance, lapRecord, lapRecordInfo, iconKey);
    }

    /**
     * Update the track (version) represented by the given track ID with the specified data.
     *
     * @param trackId       The ID of the {@link Track} to update
     * @param circuitId     The ID of the parent circuit (mandatory)
     * @param variantName   The version / layout label (optional)
     * @param distance      The track distance (in meters)
     * @param lapRecord     The lap record (in milliseconds)
     * @param lapRecordInfo Additional information about the lap record (rider, bike, year, ...), optional
     * @param iconKey       Key selecting the version's map/shape icon, optional
     * @return A {@link Track} object representing the track just updated
     */
    @PreAuthorize("hasRole('ADMIN')")
    @MutationMapping
    public Track updateTrack(@Argument long trackId,
                             @Argument Long circuitId,
                             @Argument String variantName,
                             @Argument int distance,
                             @Argument int lapRecord,
                             @Argument String lapRecordInfo,
                             @Argument String iconKey) {
        log.info("Received call to updateTrack with parameters trackId = {}, circuitId = {}, variantName = {}, distance = {}, lapRecord = {}, lapRecordInfo = {}, iconKey = {}",
                trackId, circuitId, variantName, distance, lapRecord, lapRecordInfo, iconKey);
        return trackService.updateTrack(trackId, circuitId, variantName, distance, lapRecord, lapRecordInfo, iconKey);
    }

    /**
     * Delete the track (version) represented by the given track ID.
     *
     * @param trackId The ID of the {@link Track} to delete
     * @return A {@link Track} object representing the track just deleted
     */
    @PreAuthorize("hasRole('ADMIN')")
    @MutationMapping
    public Track deleteTrack(@Argument long trackId) {
        log.info("Received call to deleteTrack with parameter trackId = {}", trackId);
        return trackService.deleteTrack(trackId);
    }

}
