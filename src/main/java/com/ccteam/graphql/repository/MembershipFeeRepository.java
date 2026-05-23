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

import com.ccteam.graphql.entities.MembershipFee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * MembershipFee JPA repository.
 *
 * @author yann39
 * @since 1.0.0
 */
@Repository
public interface MembershipFeeRepository extends CrudRepository<MembershipFee, Long> {

    /**
     * Find the membership fee record for the given member and year.
     *
     * @param memberId The member id
     * @param year     The year of the fee
     * @return The optional membership fee record
     */
    Optional<MembershipFee> findByMemberIdAndYear(Long memberId, Integer year);
}
