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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Country entity. Used as a shared reference table for any localized
 * "country" attribute (e.g. {@link Track#country}). The primary key is
 * the ISO 3166-1 alpha-2 code (e.g. "FR", "ES").
 *
 * @author yann39
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "country")
public class Country {

    /** ISO 3166-1 alpha-2 country code (e.g. "FR", "ES"). */
    @Id
    @Column(length = 2)
    private String code;

    /** Localized name in French (e.g. "France"). */
    @Column(name = "name_fr", length = 64, nullable = false)
    private String nameFr;

    /** Localized name in English (e.g. "France"). */
    @Column(name = "name_en", length = 64, nullable = false)
    private String nameEn;

    public Country(String code, String nameFr, String nameEn) {
        this.code = code;
        this.nameFr = nameFr;
        this.nameEn = nameEn;
    }

}
