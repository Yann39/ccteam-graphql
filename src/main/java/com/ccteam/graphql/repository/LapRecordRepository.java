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

import com.ccteam.graphql.entities.LapRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * {@link LapRecord} repository.
 *
 * @author yann39
 * @since 1.0.0
 */
@Repository
public interface LapRecordRepository extends JpaRepository<LapRecord, Long> {

    @Query("select distinct lr from LapRecord lr " +
            "left join fetch lr.member " +
            "left join fetch lr.track")
    List<LapRecord> findAllCustom();

    @Query("select lr from LapRecord lr " +
            "left join fetch lr.member " +
            "left join fetch lr.track " +
            "where lr.id = :id ")
    Optional<LapRecord> findByIdCustom(long id);

    @Query("select lr from LapRecord lr " +
            "left join fetch lr.member m " +
            "left join fetch lr.track " +
            "where m.id = :id")
    List<LapRecord> findByMemberIdCustom(long id);

    @Query("select lr from LapRecord lr " +
            "left join fetch lr.member " +
            "left join fetch lr.track t " +
            "where t.id = :id")
    List<LapRecord> findByTrackIdCustom(long id);

}
