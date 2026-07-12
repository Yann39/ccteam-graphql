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

import com.ccteam.graphql.entities.TrustedDevice;
import com.ccteam.graphql.model.TrustedDeviceView;
import com.ccteam.graphql.repository.TrustedDeviceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

/**
 * Manages the set of devices a member has verified and trusts for
 * passcode-only login (see {@link TrustedDevice}).
 * <p>
 * The device is identified by a high-entropy secret held on the device; the
 * server only ever stores and compares its SHA-256 hash.
 *
 * @author yann39
 * @since 1.0.3
 */
@Service
@Slf4j
public class TrustedDeviceService {

    private final TrustedDeviceRepository trustedDeviceRepository;

    public TrustedDeviceService(TrustedDeviceRepository trustedDeviceRepository) {
        this.trustedDeviceRepository = trustedDeviceRepository;
    }

    /**
     * Whether device binding is active for the member, i.e. they already have
     * at least one trusted device. Until then, passcode login is allowed from
     * any device (legacy / bootstrap) so existing installations are not broken.
     *
     * @param memberId The member id
     * @return {@code true} when the member has one or more trusted devices
     */
    public boolean memberHasAnyDevice(long memberId) {
        return trustedDeviceRepository.existsByMemberId(memberId);
    }

    /**
     * Whether the given device secret matches a trusted device of the member.
     *
     * @param memberId     The member id
     * @param deviceSecret The raw device secret sent by the client
     * @return {@code true} when the device is trusted
     */
    public boolean isTrusted(long memberId, String deviceSecret) {
        if (deviceSecret == null || deviceSecret.isBlank()) {
            return false;
        }
        return trustedDeviceRepository.findByMemberIdAndTokenHash(memberId, hash(deviceSecret)).isPresent();
    }

    /**
     * Trust the given device for the member (idempotent): creates the trusted
     * device if new, otherwise just refreshes its last-used timestamp.
     *
     * @param memberId     The member id
     * @param deviceSecret The raw device secret sent by the client
     * @param label        Optional human-readable device label
     */
    public void trustDevice(long memberId, String deviceSecret, String label) {
        if (deviceSecret == null || deviceSecret.isBlank()) {
            log.warn("Attempt to trust a device with a blank secret for member {}, ignored", memberId);
            return;
        }
        final String tokenHash = hash(deviceSecret);
        final Optional<TrustedDevice> existing = trustedDeviceRepository.findByMemberIdAndTokenHash(memberId, tokenHash);
        final TrustedDevice device = existing.orElseGet(() -> {
            final TrustedDevice d = new TrustedDevice();
            d.setMemberId(memberId);
            d.setTokenHash(tokenHash);
            d.setLabel(label);
            d.setCreatedOn(LocalDateTime.now());
            return d;
        });
        device.setLastUsedOn(LocalDateTime.now());
        trustedDeviceRepository.save(device);
        log.info("Device trusted for member {} ({})", memberId, existing.isPresent() ? "refreshed" : "new");
    }

    /**
     * Refresh the last-used timestamp of the member's trusted device after a
     * successful authentication. No-op when the device is not trusted.
     *
     * @param memberId     The member id
     * @param deviceSecret The raw device secret sent by the client
     */
    public void touch(long memberId, String deviceSecret) {
        if (deviceSecret == null || deviceSecret.isBlank()) {
            return;
        }
        trustedDeviceRepository.findByMemberIdAndTokenHash(memberId, hash(deviceSecret)).ifPresent(device -> {
            device.setLastUsedOn(LocalDateTime.now());
            trustedDeviceRepository.save(device);
        });
    }

    /**
     * List the member's trusted devices for the management screen, flagging
     * the one that made the request (matched via the current device secret).
     *
     * @param memberId      The member id
     * @param currentSecret The raw device secret of the calling device (may be null)
     * @return The member's trusted devices as views (no token hash exposed)
     */
    public List<TrustedDeviceView> listForMember(long memberId, String currentSecret) {
        final String currentHash = (currentSecret == null || currentSecret.isBlank()) ? null : hash(currentSecret);
        return trustedDeviceRepository.findByMemberIdOrderByLastUsedOnDesc(memberId).stream()
                .map(d -> new TrustedDeviceView(
                        d.getId(),
                        d.getLabel(),
                        d.getCreatedOn(),
                        d.getLastUsedOn(),
                        currentHash != null && currentHash.equals(d.getTokenHash())))
                .toList();
    }

    /**
     * Revoke (delete) one of the member's trusted devices. Ownership is
     * enforced: a device that doesn't belong to the member is left untouched.
     *
     * @param memberId The member id (from the auth token)
     * @param deviceId The id of the trusted device to revoke
     * @return {@code true} when a device was revoked, {@code false} otherwise
     */
    public boolean revoke(long memberId, long deviceId) {
        final Optional<TrustedDevice> device = trustedDeviceRepository.findById(deviceId);
        if (device.isEmpty() || !device.get().getMemberId().equals(memberId)) {
            log.info("Revoke refused: device {} not found or not owned by member {}", deviceId, memberId);
            return false;
        }
        trustedDeviceRepository.delete(device.get());
        log.info("Trusted device {} revoked for member {}", deviceId, memberId);
        return true;
    }

    /**
     * SHA-256 hex hash of the device secret. The secret is high-entropy, so a
     * plain cryptographic hash is sufficient (no need for a password KDF).
     */
    private String hash(String secret) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(secret.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is guaranteed to be available on every JVM, so this never happens
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

}
