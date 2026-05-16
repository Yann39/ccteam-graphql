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

import com.ccteam.graphql.entities.Country;
import com.ccteam.graphql.repository.CountryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@link Country} service.
 *
 * @author yann39
 * @since 1.0.0
 */
@Service
@Slf4j
public class CountryService {

    private final CountryRepository countryRepository;

    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    /**
     * List every country in the reference table, sorted by name
     */
    public List<Country> getAllCountries() {
        return countryRepository.findAll(Sort.by("nameFr"));
    }
}
