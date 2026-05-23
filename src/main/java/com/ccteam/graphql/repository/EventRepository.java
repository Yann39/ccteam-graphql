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

import com.ccteam.graphql.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * {@link Event} repository.
 *
 * @author yann39
 * @since 1.0.0
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Retrieve all events with related associations fetched (track, participants,
     * organizer, createdBy, modifiedBy). Results are ordered by start date (newest first).
     *
     * @return The list of events with associations
     */
    @Query("select e from Event e " +
            "left join fetch e.track t " +
            "left join fetch e.participants p " +
            "left join fetch p.member " +
            "left join fetch e.organizer " +
            "left join fetch e.createdBy " +
            "left join fetch e.modifiedBy " +
            "order by e.startDate desc")
    List<Event> findAllCustom();

    /**
     * Find events for the specified year.
     *
     * @param year The year to filter by
     * @return The list of events in the year
     */
    @Query("select e from Event e " +
            "left join fetch e.track t " +
            "left join fetch e.participants p " +
            "left join fetch e.organizer " +
            "left join fetch e.createdBy " +
            "left join fetch e.modifiedBy " +
            "where year(e.startDate) = :year " +
            "order by e.startDate desc")
    List<Event> findByYearCustom(int year);

    /**
     * Find events in the given month and year.
     *
     * @param month The month number (1-12)
     * @param year  The year number
     * @return The list of events matching the month and year
     */
    @Query("select e from Event e " +
            "left join fetch e.track t " +
            "left join fetch e.participants p " +
            "left join fetch e.organizer " +
            "left join fetch e.createdBy " +
            "left join fetch e.modifiedBy " +
            "where month(e.startDate) = :month " +
            "and year(e.startDate) = :year " +
            "order by e.startDate desc")
    List<Event> findByMonthAndYearCustom(int month, int year);

    /**
     * Find events on the specified day, month and year.
     *
     * @param day   The day of month
     * @param month The month number (1-12)
     * @param year  The year number
     * @return The list of events on that date
     */
    @Query("select e from Event e " +
            "left join fetch e.track t " +
            "left join fetch e.participants p " +
            "left join fetch e.organizer " +
            "left join fetch e.createdBy " +
            "left join fetch e.modifiedBy " +
            "where day(e.startDate) = :day " +
            "and month(e.startDate) = :month " +
            "and year(e.startDate) = :year " +
            "order by e.startDate desc")
    List<Event> findByDayAndMonthAndYearCustom(int day, int month, int year);

    /**
     * Find an event by id and eagerly fetch participants, track, organizer and audit fields.
     *
     * @param id The event id
     * @return The optional event with associations fetched
     */
    @Query("select e from Event e " +
            "left join fetch e.track t " +
            "left join fetch e.participants p " +
            "left join fetch p.member " +
            "left join fetch p.bike " +
            "left join fetch e.organizer " +
            "left join fetch e.createdBy " +
            "left join fetch e.modifiedBy " +
            "where e.id = :id")
    Optional<Event> findByIdCustom(long id);

    /**
     * Find events whose title contains the given text. When {@code title} is null returns all events.
     * Associations are fetched for display.
     *
     * @param title The filter text (nullable)
     * @return The list of matching events
     */
    @Query("select e from Event e " +
            "left join fetch e.track t " +
            "left join fetch e.participants p " +
            "left join fetch p.member " +
            "left join fetch e.organizer " +
            "left join fetch e.createdBy " +
            "left join fetch e.modifiedBy " +
            "where :title is null or e.title like %:title% " +
            "order by e.startDate desc")
    List<Event> findByTitleCustom(String title);

}
