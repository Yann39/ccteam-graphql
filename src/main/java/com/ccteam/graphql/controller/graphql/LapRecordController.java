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

import com.ccteam.graphql.entities.LapRecord;
import com.ccteam.graphql.service.LapRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * {@link LapRecord} GraphQL controller.
 *
 * @author yann39
 * @since 1.0.0
 */
@Controller
@Slf4j
public class LapRecordController {

    private final LapRecordService lapRecordService;

    public LapRecordController(LapRecordService lapRecordService) {
        this.lapRecordService = lapRecordService;
    }

    /**
     * Get all lap records.
     *
     * @return A list of {@link LapRecord} objects representing the lap records
     */
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @QueryMapping
    public List<LapRecord> getAllLapRecords() {
        log.info("Received call to getAllLapRecords");
        return lapRecordService.getAllLapRecords();
    }

    /**
     * Get all lap records for the specified member given its ID.
     *
     * @param memberId The ID of the member for which to retrieve lap records
     * @return A list of {@link LapRecord} objects representing the lap records
     */
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @QueryMapping
    public List<LapRecord> getMemberLapRecords(@Argument Long memberId) {
        log.info("Received call to getMemberLapRecords with parameters memberId = {}", memberId);
        return lapRecordService.getLapRecordsByMember(memberId);
    }

    /**
     * Get all lap records for the specified track given its ID.
     *
     * @param trackId The ID of the track for which to retrieve lap records
     * @return A list of {@link LapRecord} objects representing the lap records
     */
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @QueryMapping
    public List<LapRecord> getTrackLapRecords(@Argument Long trackId) {
        log.info("Received call to getTrackLapRecords with parameters trackId = {}", trackId);
        return lapRecordService.getLapRecordsByTrack(trackId);
    }

    /**
     * Create a new lap record.
     *
     * @param memberId   The member ID
     * @param trackId    The track ID
     * @param recordDate The lap record date as ISO 8601 string
     * @param lapTime    The lap time in milliseconds
     * @param conditions The track conditions
     * @param comments   Some comment about the lap record
     * @return A {@link LapRecord} object representing the lap record just created
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @MutationMapping
    public LapRecord createLapRecord(@Argument long memberId,
                                     @Argument long trackId,
                                     @Argument String recordDate,
                                     @Argument int lapTime,
                                     @Argument String conditions,
                                     @Argument String comments) {
        log.info("Received call to createLapRecord with parameters memberId = {}, trackId = {}, recordDate = {}, lapTime = {}, conditions = {}, comments = {}",
                memberId, trackId, recordDate, lapTime, conditions, comments);
        return lapRecordService.createLapRecord(memberId, trackId, recordDate, lapTime, conditions, comments);
    }

    /**
     * Update the lap record represented by the given lap record ID with the specified data.
     *
     * @param lapRecordId The lap record ID
     * @param trackId     The track ID
     * @param recordDate  The lap record date as ISO 8601 string
     * @param lapTime     The lap time in milliseconds
     * @param conditions  The track conditions
     * @param comments    Some comment about the lap record
     * @return A {@link LapRecord} object representing the lap record just created
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @MutationMapping
    public LapRecord updateLapRecord(@Argument long lapRecordId,
                                     @Argument long trackId,
                                     @Argument String recordDate,
                                     @Argument int lapTime,
                                     @Argument String conditions,
                                     @Argument String comments) {
        log.info("Received call to updateLapRecord with parameters lapRecordId = {}, trackId = {}, recordDate = {}, lapTime = {}, conditions = {}, comments = {}",
                lapRecordId, trackId, recordDate, lapTime, conditions, comments);
        return lapRecordService.updateLapRecord(lapRecordId, trackId, recordDate, lapTime, conditions, comments);
    }

    /**
     * Delete the lap record represented by the given lap record ID.
     *
     * @param lapRecordId The ID of the {@link LapRecord} to delete
     * @return A {@link LapRecord} object representing the lap record just deleted
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @MutationMapping
    public LapRecord deleteLapRecord(@Argument long lapRecordId) {
        log.info("Received call to deleteLapRecord with parameters lapRecordId = {}", lapRecordId);
        return lapRecordService.deleteLapRecord(lapRecordId);
    }

}