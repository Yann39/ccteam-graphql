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
import com.ccteam.graphql.entities.Country;
import com.ccteam.graphql.entities.News;
import com.ccteam.graphql.entities.Track;
import com.ccteam.graphql.repository.CountryRepository;
import com.ccteam.graphql.repository.TrackRepository;
import lombok.extern.slf4j.Slf4j;
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

    private final TrackRepository trackRepository;
    private final CountryRepository countryRepository;

    public TrackService(TrackRepository trackRepository, CountryRepository countryRepository) {
        this.trackRepository = trackRepository;
        this.countryRepository = countryRepository;
    }

    /**
     * Resolve a {@link Country} from its ISO 3166-1 alpha-2 code. The
     * country is mandatory on tracks, so a {@code null} or blank code is
     * rejected.
     *
     * @param countryCode the country code (mandatory)
     * @return the matching {@link Country}
     * @throws CustomGraphQLException if the code is missing or does not
     *         match any known country
     */
    private Country resolveCountry(String countryCode) {
        if (countryCode == null || countryCode.isBlank()) {
            log.error("Country code is required for track creation/update");
            throw new CustomGraphQLException("country_required",
                    "A country code is required");
        }
        return countryRepository.findById(countryCode.toUpperCase())
                .orElseThrow(() -> {
                    log.error("Country with code {} not found", countryCode);
                    return new CustomGraphQLException("country_not_found",
                            "Specified country code has not been found");
                });
    }

    /**
     * Get all tracks.
     *
     * @return A list of {@link Track} objects representing the tracks
     */
    public List<Track> getAllTracks() {
        return trackRepository.findAllCustom();
    }

    /**
     * Get all tracks according to the specified filter {@code text}.<br/>
     * Search is done on track name.<br/>
     * If {@code text} filter is null, all records will be returned.
     *
     * @param text The text filter string
     * @return A list of {@link Track} objects representing the tracks
     */
    public List<Track> getTracksFiltered(String text) {
        return trackRepository.findFilteredCustom(text);
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
     * Create a new track.
     *
     * @param name        The official name of the track
     * @param distance    The track distance (in meters)
     * @param lapRecord   The lap record (in milliseconds)
     * @param website     The official website of the track
     * @param latitude    The track latitude coordinate
     * @param longitude   The track longitude coordinate
     * @param countryCode ISO 3166-1 alpha-2 code of the country where the
     *                    track is located (optional)
     * @return A {@link Track} object representing the track just created
     */
    public Track createTrack(String name, int distance, int lapRecord, String website, BigDecimal latitude,
            BigDecimal longitude, String countryCode) {
        final Track track = new Track();
        track.setName(name);
        track.setDistance(distance);
        track.setLapRecord(lapRecord);
        track.setWebsite(website);
        track.setLatitude(latitude);
        track.setLongitude(longitude);
        track.setCountry(resolveCountry(countryCode));
        return trackRepository.save(track);
    }

    /**
     * Update the track represented by the given track ID with the specified data.
     *
     * @param name        The official name of the track
     * @param distance    The track distance (in meters)
     * @param lapRecord   The lap record (in milliseconds)
     * @param website     The official website of the track
     * @param latitude    The track latitude coordinate
     * @param longitude   The track longitude coordinate
     * @param countryCode ISO 3166-1 alpha-2 code of the country where the
     *                    track is located (optional)
     * @return A {@link Track} object representing the track just updated
     */
    public Track updateTrack(long trackId, String name, int distance, int lapRecord, String website,
            BigDecimal latitude, BigDecimal longitude, String countryCode) {
        final Optional<Track> trackOptional = trackRepository.findByIdCustom(trackId);
        if (trackOptional.isEmpty()) {
            log.error("Track with id {} not found in the database", trackId);
            throw new CustomGraphQLException("track_not_found",
                    "Specified track ID has not been found in the database");
        }

        final Track track = trackOptional.get();
        track.setName(name);
        track.setDistance(distance);
        track.setLapRecord(lapRecord);
        track.setWebsite(website);
        track.setLatitude(latitude);
        track.setLongitude(longitude);
        track.setCountry(resolveCountry(countryCode));
        return trackRepository.save(track);
    }

    /**
     * Delete the track represented by the given track ID.
     *
     * @param trackId The ID of the {@link Track} to delete
     * @return A {@link Track} object representing the track just deleted
     */
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