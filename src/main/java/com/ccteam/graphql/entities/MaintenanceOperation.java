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
import com.ccteam.graphql.enums.MaintenanceOperationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * An operation performed during a {@link Maintenance} (oil change, brake pads, ...).
 *
 * @author yann39
 * @since 1.3.0
 */
@Getter
@Setter
@Entity
@Table(name = "maintenance_operation")
public class MaintenanceOperation {

    /**
     * Database identifier (primary key) for this operation.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The maintenance this operation belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maintenance_id", nullable = false)
    private Maintenance maintenance;

    /**
     * 0-based rank of the operation within the maintenance, to keep the order in which they were entered.
     */
    @Column(nullable = false)
    private Integer position;

    /**
     * Kind of operation.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 32, nullable = false)
    private MaintenanceOperationType type;

    /**
     * Free label, maximum 500 characters, possibly on several lines. Required for
     * {@link MaintenanceOperationType#OTHER}, optional precision otherwise (e.g. the oil brand and reference).
     */
    @Column(length = 500)
    private String label;

    /**
     * Price of the operation, in {@link #currency} (optional).
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Currency of the price. Set per operation since the parts of a same maintenance may have been bought in
     * different countries.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 3, nullable = false)
    private CurrencyCode currency = CurrencyCode.CHF;

}
