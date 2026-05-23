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

package com.ccteam.graphql.controller.graphql;

import com.ccteam.graphql.entities.Organizer;
import com.ccteam.graphql.service.OrganizerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * {@link Organizer} GraphQL controller.
 *
 * @author yann39
 * @since 1.0.0
 */
@Controller
@Slf4j
public class OrganizerController {

    private final OrganizerService organizerService;

    public OrganizerController(OrganizerService organizerService) {
        this.organizerService = organizerService;
    }

    /**
     * Get the list of every organizer, sorted by name.
     * USER-level so the event picker can fetch them even before the caller is a fully-vetted member.
     *
     * @return The list of every organizer, sorted by name
     */
    @PreAuthorize("hasRole('USER')")
    @QueryMapping
    public List<Organizer> getAllOrganizers() {
        log.info("Received call to getAllOrganizers");
        return organizerService.getAllOrganizers();
    }

    /**
     * Create a new organizer.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @MutationMapping
    public Organizer createOrganizer(@Argument String name) {
        log.info("Received call to createOrganizer with parameter name = {}", name);
        return organizerService.createOrganizer(name);
    }
}
