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
import com.ccteam.graphql.entities.Organizer;
import com.ccteam.graphql.repository.OrganizerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * {@link Organizer} service.
 *
 * @author yann39
 * @since 1.0.0
 */
@Service
@Slf4j
public class OrganizerService {

    private final OrganizerRepository organizerRepository;

    public OrganizerService(OrganizerRepository organizerRepository) {
        this.organizerRepository = organizerRepository;
    }

    /**
     * List every organizer in the database, sorted alphabetically by name.
     */
    public List<Organizer> getAllOrganizers() {
        return organizerRepository.findAll(Sort.by("name"));
    }

    /**
     * Create a new {@link Organizer} from a free-text name.
     *
     * @param rawName the organizer name as submitted from the form
     * @return the newly created {@link Organizer}
     */
    @Transactional
    public Organizer createOrganizer(String rawName) {
        if (rawName == null) {
            throw new CustomGraphQLException("organizer_name_required", "Organizer name is required");
        }
        final String name = rawName.trim();
        if (name.isEmpty()) {
            throw new CustomGraphQLException("organizer_name_required", "Organizer name is required");
        }
        final Optional<Organizer> existing = organizerRepository.findByNameIgnoreCaseOrderByName(name);
        if (existing.isPresent()) {
            log.info("Organizer with name '{}' already exists (id {})", name, existing.get().getId());
            throw new CustomGraphQLException("organizer_already_exists", "An organizer with this name already exists");
        }
        final Organizer fresh = new Organizer();
        fresh.setName(name);
        return organizerRepository.save(fresh);
    }
}
