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
import com.ccteam.graphql.entities.Member;
import com.ccteam.graphql.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * REST account controller.
 *
 * @author yann39
 * @since 1.0.0
 */
@RestController
@CrossOrigin
@Component
@Slf4j
public class UploadController {

    @Autowired
    private MemberRepository memberRepository;

    /**
     * Set the specified file as avatar for the specified member ID.
     *
     * @param memberId The {@link Member} ID
     * @param file     The file representing the avatar
     * @return An empty body response with one of the following HTTP status :
     * <ul>
     * <li>400 Bad request if member ID is missing from the request</li>
     * <li>400 Bad request if file is missing from the request</li>
     * <li>404 Not Found if no member has been found for the specified member ID</li>
     * <li>500 Internal Server Error if file bytes from the specified avatar file failed to be read</li>
     * <li>200 Ok if the avatar file has been successfully associated to the member</li>
     * </ul>
     */
    @RequestMapping(value = "/rest/uploadAvatar", method = RequestMethod.POST)
    public ResponseEntity<?> uploadAvatar(@RequestParam("memberId") Long memberId, @RequestParam("file") MultipartFile file) {

        log.info("Call to checkAccount REST endpoint");

        // member ID has not been specified
        if (memberId == null) {
            log.info("No member ID specified");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // file has not been specified
        if (file == null) {
            log.info("No file specified");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // get any member with the specified ID
        final Optional<Member> member = memberRepository.findByIdCustom(memberId);

        // e-mail address does not exist
        if (member.isEmpty()) {
            log.info("Member with ID {} has not been found", memberId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        final Attachment attachment = new Attachment();

        try {
            attachment.setFile(file.getBytes());
        } catch (IOException e) {
            log.info("Exception occurred while reading file bytes for file {}", file.getName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        attachment.setFilename(file.getName());

        member.get().setAvatar(attachment);
        attachment.setUploadDate(LocalDateTime.now());

        memberRepository.save(member.get());

        log.info("Avatar file {} has been successfully set for member ID {}", file.getName(), memberId);
        return ResponseEntity.status(HttpStatus.OK).build();

    }

}