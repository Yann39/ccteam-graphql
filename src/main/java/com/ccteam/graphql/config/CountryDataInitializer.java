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

package com.ccteam.graphql.config;

import com.ccteam.graphql.entities.Country;
import com.ccteam.graphql.repository.CountryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds the {@code country} reference table at application startup with
 * the European countries most commonly relevant for the CCTeam app
 * (motorbike track-day events). Idempotent: existing rows (matched by
 * primary key, the ISO 3166-1 alpha-2 code) are not overwritten.
 *
 * @author yann39
 * @since 1.0.0
 */
@Component
@Slf4j
public class CountryDataInitializer implements CommandLineRunner {

    private final CountryRepository countryRepository;

    public CountryDataInitializer(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public void run(String... args) {
        final List<Country> seed = List.of(
                new Country("FR", "France", "France"),
                new Country("ES", "Espagne", "Spain"),
                new Country("IT", "Italie", "Italy"),
                new Country("DE", "Allemagne", "Germany"),
                new Country("BE", "Belgique", "Belgium"),
                new Country("NL", "Pays-Bas", "Netherlands"),
                new Country("PT", "Portugal", "Portugal"),
                new Country("CH", "Suisse", "Switzerland"),
                new Country("AT", "Autriche", "Austria"),
                new Country("GB", "Royaume-Uni", "United Kingdom"),
                new Country("LU", "Luxembourg", "Luxembourg")
        );

        int created = 0;
        for (Country country : seed) {
            if (!countryRepository.existsById(country.getCode())) {
                countryRepository.save(country);
                created++;
            }
        }
        if (created > 0) {
            log.info("Seeded {} new country/countries (out of {} reference entries)", created, seed.size());
        } else {
            log.info("Country reference table already up to date ({} entries)", seed.size());
        }
    }
}
