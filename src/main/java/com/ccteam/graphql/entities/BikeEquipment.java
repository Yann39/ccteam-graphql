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

package com.ccteam.graphql.entities;

import com.ccteam.graphql.enums.CurrencyCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * A part fitted to a bike (racing exhaust, short brake lever, crash pads, ...).
 *
 * @author yann39
 * @since 1.2.4
 */
@Getter
@Setter
@Entity
@Table(name = "bike_equipment")
public class BikeEquipment {

    /**
     * Database identifier (primary key) for this equipment.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The bike the part is fitted to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_id", nullable = false)
    private Bike bike;

    /**
     * 0-based rank of the equipment in the list of the bike, in the order set by the member.
     */
    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer position = 0;

    /**
     * Name of the part, maximum 128 characters (e.g. "Échappement Akrapovič Slip-On").
     */
    @Column(length = 128, nullable = false)
    private String designation;

    /**
     * Price of the part, in {@link #currency} (optional).
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Currency of the price.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 3, nullable = false)
    private CurrencyCode currency = CurrencyCode.CHF;

    /**
     * Free comment, maximum 500 characters, possibly on several lines (reference, where it was bought, ...).
     */
    @Column(length = 500)
    private String comment;

    /**
     * Timestamp when the equipment was created.
     */
    @Column(nullable = false)
    private LocalDateTime createdOn;

    /**
     * Timestamp when the equipment was last modified.
     */
    @Column
    private LocalDateTime modifiedOn;

}
