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

package com.ccteam.graphql.service;

import com.ccteam.graphql.config.graphql.CustomGraphQLException;
import com.ccteam.graphql.entities.Circuit;
import com.ccteam.graphql.entities.Track;
import com.ccteam.graphql.repository.CircuitRepository;
import com.ccteam.graphql.repository.TrackRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * {@link Track} service. A track is a single version (layout) of a {@link Circuit}.
 *
 * @author yann39
 * @since 1.0.0
 */
@Service
@Slf4j
public class TrackService {

    private final TrackRepository trackRepository;
    private final CircuitRepository circuitRepository;

    public TrackService(TrackRepository trackRepository, CircuitRepository circuitRepository) {
        this.trackRepository = trackRepository;
        this.circuitRepository = circuitRepository;
    }

    /**
     * Resolve the parent {@link Circuit} from its id. The circuit is mandatory on a version,
     * so a {@code null} id or an unknown circuit is rejected.
     *
     * @param circuitId The circuit id (mandatory)
     * @return The matching {@link Circuit}
     * @throws CustomGraphQLException If the id is missing or does not match any circuit
     */
    private Circuit resolveCircuit(Long circuitId) {
        if (circuitId == null) {
            log.error("Circuit id is required for track creation/update");
            throw new CustomGraphQLException("circuit_required", "A circuit is required");
        }
        return circuitRepository.findById(circuitId)
                .orElseThrow(() -> {
                    log.error("Circuit with id {} not found", circuitId);
                    return new CustomGraphQLException("circuit_not_found", "Specified circuit has not been found");
                });
    }

    /**
     * Normalize an optional free-text value: trimmed, or {@code null} when blank.
     */
    private String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }
        final String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /**
     * Get all tracks (versions).
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
            log.error("Track with id {} not found in the database", id);
            throw new CustomGraphQLException("track_not_found", "Specified track has not been found in the database");
        }
        return trackOptional.get();
    }

    /**
     * Create a new track (version) attached to a circuit.
     *
     * @param circuitId     The ID of the parent {@link Circuit} (mandatory)
     * @param variantName   The version / layout label (optional, e.g. "5,8 km GP")
     * @param distance      The track distance (in meters)
     * @param lapRecord     The lap record (in milliseconds)
     * @param lapRecordInfo Additional information about the lap record (rider, bike, year, ...), optional
     * @param iconKey       Key selecting the version's map/shape icon, optional
     * @return A {@link Track} object representing the track just created
     */
    @Transactional
    public Track createTrack(Long circuitId, String variantName, int distance, int lapRecord, String lapRecordInfo, String iconKey) {
        final Track track = new Track();
        track.setCircuit(resolveCircuit(circuitId));
        track.setVariantName(normalizeOptional(variantName));
        track.setDistance(distance);
        track.setLapRecord(lapRecord);
        track.setLapRecordInfo(normalizeOptional(lapRecordInfo));
        track.setIconKey(normalizeOptional(iconKey));
        return trackRepository.save(track);
    }

    /**
     * Update the track (version) represented by the given track ID with the specified data.
     *
     * @param trackId       The ID of the {@link Track} to update
     * @param circuitId     The ID of the parent {@link Circuit} (mandatory)
     * @param variantName   The version / layout label (optional)
     * @param distance      The track distance (in meters)
     * @param lapRecord     The lap record (in milliseconds)
     * @param lapRecordInfo Additional information about the lap record (rider, bike, year, ...), optional
     * @param iconKey       Key selecting the version's map/shape icon, optional
     * @return A {@link Track} object representing the track just updated
     */
    @Transactional
    public Track updateTrack(long trackId, Long circuitId, String variantName, int distance, int lapRecord, String lapRecordInfo, String iconKey) {
        final Optional<Track> trackOptional = trackRepository.findByIdCustom(trackId);
        if (trackOptional.isEmpty()) {
            log.error("Track with id {} not found in the database", trackId);
            throw new CustomGraphQLException("track_not_found",
                    "Specified track ID has not been found in the database");
        }

        final Track track = trackOptional.get();
        track.setCircuit(resolveCircuit(circuitId));
        track.setVariantName(normalizeOptional(variantName));
        track.setDistance(distance);
        track.setLapRecord(lapRecord);
        track.setLapRecordInfo(normalizeOptional(lapRecordInfo));
        track.setIconKey(normalizeOptional(iconKey));
        return trackRepository.save(track);
    }

    /**
     * Delete the track (version) represented by the given track ID.
     *
     * @param trackId The ID of the {@link Track} to delete
     * @return A {@link Track} object representing the track just deleted
     */
    @Transactional
    public Track deleteTrack(long trackId) {
        final Optional<Track> trackOptional = trackRepository.findByIdCustom(trackId);
        if (trackOptional.isEmpty()) {
            log.error("Track with id {} not found in the database", trackId);
            throw new CustomGraphQLException("track_not_found",
                    "Specified track ID has not been found in the database");
        }

        final Track track = trackOptional.get();
        trackRepository.delete(track);
        return track;
    }

}
