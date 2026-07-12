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

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * A device the member has verified (via e-mail OTP) and that is therefore
 * trusted to log in with the passcode alone.
 * <p>
 * The device is identified by a high-entropy secret generated and stored
 * on the device (Android Keystore); only its SHA-256 hash is stored here,
 * so the row is useless to an attacker who reads the database. The member
 * is referenced by its plain id (no foreign key), mirroring
 * {@link EventReminder}: leftover rows of a deleted member are harmless
 * since that member can no longer authenticate.
 *
 * @author yann39
 * @since 1.0.3
 */
@Getter
@Setter
@Entity
@Table(name = "trusted_device", uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "token_hash"}))
public class TrustedDevice {

    /**
     * Database identifier (primary key) for this trusted device.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID of the {@link Member} the device belongs to.
     */
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    /**
     * SHA-256 hash (hex) of the device secret. Never store the secret itself.
     */
    @Column(name = "token_hash", length = 64, nullable = false)
    private String tokenHash;

    /**
     * Optional human-readable label for the device (shown in a future
     * "trusted devices" management screen).
     */
    @Column(length = 128)
    private String label;

    /**
     * Timestamp when the device was first trusted.
     */
    @Column(nullable = false)
    private LocalDateTime createdOn = LocalDateTime.now();

    /**
     * Timestamp of the last successful authentication from this device.
     */
    @Column
    private LocalDateTime lastUsedOn;

}
