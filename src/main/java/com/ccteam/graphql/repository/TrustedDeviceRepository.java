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

import com.ccteam.graphql.entities.TrustedDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * {@link TrustedDevice} repository.
 *
 * @author yann39
 * @since 1.0.3
 */
@Repository
public interface TrustedDeviceRepository extends JpaRepository<TrustedDevice, Long> {

    /**
     * Whether the given member has at least one trusted device. When false,
     * device binding is not yet active for that member (legacy / bootstrap).
     *
     * @param memberId The member id
     * @return {@code true} when the member has one or more trusted devices
     */
    boolean existsByMemberId(long memberId);

    /**
     * Find the trusted device for the given member and token hash, if any.
     *
     * @param memberId  The member id
     * @param tokenHash The SHA-256 hash of the device secret
     * @return The matching trusted device, or empty
     */
    Optional<TrustedDevice> findByMemberIdAndTokenHash(long memberId, String tokenHash);

    /**
     * List the member's trusted devices, most recently used first.
     *
     * @param memberId The member id
     * @return The member's trusted devices
     */
    List<TrustedDevice> findByMemberIdOrderByLastUsedOnDesc(long memberId);

}
