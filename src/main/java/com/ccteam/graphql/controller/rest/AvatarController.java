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

package com.ccteam.graphql.controller.rest;

import com.ccteam.graphql.entities.Attachment;
import com.ccteam.graphql.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.ZoneOffset;
import java.util.Optional;

/**
 * Serves member avatar binaries over HTTP, with proper caching headers so clients can avoid re-downloading
 * the bytes on every render.
 * <p>
 * Lives outside GraphQL on purpose: avatars are large opaque blobs that are not useful to GraphQL clients,
 * and we want to be able to serve them with long-term caching headers.
 * <p>
 * The HTTP endpoint route lets us emit a stable {@code ETag} and honour {@code If-None-Match} for
 * a {@code 304 Not Modified}, plus sit behind a CDN later if scale ever demands it.
 *
 * @author yann39
 * @since 1.0.0
 */
@RestController
@RequestMapping("/avatars")
@Slf4j
public class AvatarController {

    /**
     * Browser/client may reuse the cached body for an hour before re-validating with the server.
     * With {@code must-revalidate}, past that TTL the client must check freshness (sends {@code If-None-Match})
     * before reusing, server then answers 304 with empty body when the ETag still matches.
     */
    private static final Duration CACHE_MAX_AGE = Duration.ofHours(1);

    private final MemberRepository memberRepository;

    public AvatarController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * Serve the avatar for the given member id.
     * <ul>
     *   <li>{@code 200 OK} with the raw bytes + {@code ETag} and {@code Cache-Control} when the avatar exists.</li>
     *   <li>{@code 304 Not Modified} (empty body) when the client sends a matching {@code If-None-Match} header,
     *       the only round-trip cost is a few HTTP headers.</li>
     *   <li>{@code 404 Not Found} when the member doesn't exist or hasn't uploaded an avatar.</li>
     * </ul>
     *
     * @param memberId    the member whose avatar to fetch
     * @param ifNoneMatch the client's cached etag, if any
     * @return the avatar bytes, a 304, or a 404
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{memberId}")
    public ResponseEntity<byte[]> getAvatar(@PathVariable long memberId,
                                            @RequestHeader(value = HttpHeaders.IF_NONE_MATCH, required = false) String ifNoneMatch) {
        log.info("Avatar request for member {} (If-None-Match={})", memberId, ifNoneMatch);

        final Optional<Attachment> avatarOpt = memberRepository.findAvatarByMemberId(memberId);
        if (avatarOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        final Attachment a = avatarOpt.get();

        // etag combines the attachment id and its upload timestamp, if they change it invalidates the client's cache automatically
        final String etag = '"' + a.getId() + "-" + a.getUploadDate().toEpochSecond(ZoneOffset.UTC) + '"';

        if (etag.equals(ifNoneMatch)) {
            // client has the current version cached, short-circuit with a 304 (no body)
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
                    .eTag(etag)
                    .cacheControl(CacheControl.maxAge(CACHE_MAX_AGE).cachePrivate().mustRevalidate())
                    .build();
        }

        return ResponseEntity.ok()
                .eTag(etag)
                .cacheControl(CacheControl.maxAge(CACHE_MAX_AGE).cachePrivate().mustRevalidate())
                .contentType(mediaTypeFromFilename(a.getFilename()))
                .body(a.getFile());
    }

    /**
     * Best-effort Content-Type from the stored filename extension.
     * Defaults to {@code application/octet-stream} when nothing matches.
     */
    private MediaType mediaTypeFromFilename(String filename) {
        if (filename == null) return MediaType.APPLICATION_OCTET_STREAM;
        final String lower = filename.toLowerCase();
        if (lower.endsWith(".png")) return MediaType.IMAGE_PNG;
        if (lower.endsWith(".gif")) return MediaType.IMAGE_GIF;
        if (lower.endsWith(".webp")) return MediaType.valueOf("image/webp");
        // .jpg / .jpeg / unknown → JPEG, the most common avatar format
        return MediaType.IMAGE_JPEG;
    }
}
