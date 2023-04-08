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

package com.chachatte.graphql.repository;

import com.chachatte.graphql.entities.Event;
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

    @Query("select e from Event e " +
            "left join fetch e.track t " +
            "left join fetch e.participants p " +
            "left join fetch p.member " +
            "left join fetch e.createdBy " +
            "left join fetch e.modifiedBy")
    List<Event> findAllCustom();

    @Query("select e from Event e " +
            "left join fetch e.track t " +
            "left join fetch e.participants p " +
            "left join fetch p.member " +
            "left join fetch e.createdBy " +
            "left join fetch e.modifiedBy " +
            "where year(e.startDate) = :year")
    List<Event> findByYearCustom(int year);

    @Query("select e from Event e " +
            "left join fetch e.track t " +
            "left join fetch e.participants p " +
            "left join fetch p.member " +
            "left join fetch e.createdBy " +
            "left join fetch e.modifiedBy " +
            "where month(e.startDate) = :month " +
            "and year(e.startDate) = :year")
    List<Event> findByMonthAndYearCustom(int month, int year);

    @Query("select e from Event e " +
            "left join fetch e.track t " +
            "left join fetch e.participants p " +
            "left join fetch p.member " +
            "left join fetch e.createdBy " +
            "left join fetch e.modifiedBy " +
            "where day(e.startDate) = :day " +
            "and month(e.startDate) = :month " +
            "and year(e.startDate) = :year")
    List<Event> findByDayAndMonthAndYearCustom(int day, int month, int year);

    @Query("select e from Event e " +
            "left join fetch e.track t " +
            "left join fetch e.participants p " +
            "left join fetch p.member " +
            "left join fetch e.createdBy " +
            "left join fetch e.modifiedBy " +
            "where e.id = :id")
    Optional<Event> findByIdCustom(long id);

    @Query("select e from Event e " +
            "left join fetch e.track t " +
            "left join fetch e.participants p " +
            "left join fetch p.member " +
            "left join fetch e.createdBy " +
            "left join fetch e.modifiedBy " +
            "where :title is null or e.title like %:title%")
    List<Event> findByTitleCustom(String title);

}
