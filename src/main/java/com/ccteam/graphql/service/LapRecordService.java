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
import com.ccteam.graphql.entities.LapRecord;
import com.ccteam.graphql.entities.Member;
import com.ccteam.graphql.entities.Track;
import com.ccteam.graphql.repository.LapRecordRepository;
import com.ccteam.graphql.repository.MemberRepository;
import com.ccteam.graphql.repository.TrackRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * {@link LapRecord} service.
 *
 * @author yann39
 * @since 1.0.0
 */
@Service
@Slf4j
public class LapRecordService {

    private final LapRecordRepository lapRecordRepository;
    private final MemberRepository memberRepository;
    private final TrackRepository trackRepository;

    public LapRecordService(LapRecordRepository lapRecordRepository, MemberRepository memberRepository, TrackRepository trackRepository) {
        this.lapRecordRepository = lapRecordRepository;
        this.memberRepository = memberRepository;
        this.trackRepository = trackRepository;
    }

    /**
     * Get all lap records.
     *
     * @return A list of {@link LapRecord} objects representing the members
     */
    public List<LapRecord> getAllLapRecords() {
        return lapRecordRepository.findAllCustom();
    }

    /**
     * Get all lap records for the specified member.
     *
     * @param memberId The ID of the member for which to retrieve the lap records
     * @return A list of {@link LapRecord} objects representing the lap records
     */
    public List<LapRecord> getLapRecordsByMember(long memberId) {
        return lapRecordRepository.findByMemberIdCustom(memberId);
    }

    /**
     * Get all lap records for the specified track.
     *
     * @param trackId The ID of the track for which to retrieve the lap records
     * @return A list of {@link LapRecord} objects representing the lap records
     */
    public List<LapRecord> getLapRecordsByTrack(long trackId) {
        return lapRecordRepository.findByTrackIdCustom(trackId);
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
    public LapRecord createLapRecord(long memberId, long trackId, String recordDate, int lapTime, String conditions, String comments) {

        final Optional<Member> memberOptional = memberRepository.findByIdCustom(memberId);
        if (memberOptional.isEmpty()) {
            log.error("Member with ID {} has not been found in the database", memberId);
            throw new CustomGraphQLException("member_not_found", "Specified member ID has not been found in the database");
        }

        final Optional<Track> trackOptional = trackRepository.findByIdCustom(trackId);
        if (trackOptional.isEmpty()) {
            log.error("Track with ID {} has not been found in the database", trackId);
            throw new CustomGraphQLException("track_not_found", "Specified track ID has not been found in the database");
        }

        final LapRecord lapRecord = new LapRecord();
        lapRecord.setMember(memberOptional.get());
        lapRecord.setTrack(trackOptional.get());
        lapRecord.setRecordDate(LocalDateTime.parse(recordDate));
        lapRecord.setLapTime(lapTime);
        lapRecord.setConditions(conditions);
        lapRecord.setComments(comments);
        lapRecord.setCreatedOn(LocalDateTime.now());

        return lapRecordRepository.save(lapRecord);
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
    public LapRecord updateLapRecord(long lapRecordId, long trackId, String recordDate, int lapTime, String conditions, String comments) {

        final Optional<LapRecord> lapRecordOptional = lapRecordRepository.findByIdCustom(lapRecordId);
        if (lapRecordOptional.isEmpty()) {
            log.error("Lap record with id {} not found in the database", lapRecordId);
            throw new CustomGraphQLException("lap_record_not_found", "Specified lap record ID has not been found in the database");
        }

        final Optional<Track> trackOptional = trackRepository.findByIdCustom(trackId);
        if (trackOptional.isEmpty()) {
            log.error("Track with ID {} has not been found in the database", trackId);
            throw new CustomGraphQLException("track_not_found", "Specified track ID has not been found in the database");
        }

        final LapRecord lapRecord = lapRecordOptional.get();

        lapRecord.setTrack(trackOptional.get());
        lapRecord.setRecordDate(LocalDateTime.parse(recordDate));
        lapRecord.setLapTime(lapTime);
        lapRecord.setConditions(conditions);
        lapRecord.setComments(comments);
        lapRecord.setModifiedOn(LocalDateTime.now());

        return lapRecordRepository.save(lapRecord);
    }

    /**
     * Delete the lap record represented by the given lap record ID.
     *
     * @param lapRecordId The ID of the {@link LapRecord} to delete
     * @return A {@link LapRecord} object representing the lap record just deleted
     */
    public LapRecord deleteLapRecord(long lapRecordId) {
        final Optional<LapRecord> lapRecordOptional = lapRecordRepository.findByIdCustom(lapRecordId);
        if (lapRecordOptional.isEmpty()) {
            log.error("Lap record with id {} not found in the database", lapRecordId);
            throw new CustomGraphQLException("lap_record_not_found", "Specified lap record ID has not been found in the database");
        }

        final LapRecord lapRecord = lapRecordOptional.get();
        lapRecordRepository.delete(lapRecord);
        return lapRecord;
    }

}