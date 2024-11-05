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

import com.ccteam.graphql.entities.Member;
import com.ccteam.graphql.model.DeleteAccountRequest;
import com.ccteam.graphql.repository.MemberRepository;
import com.ccteam.graphql.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * REST account controller.
 *
 * @author yann39
 * @since 1.0.0
 */
@CrossOrigin
@Component
@Slf4j
@Controller
public class WebController {

    private final MemberRepository memberRepository;
    private final MailService mailService;

    public WebController(MemberRepository memberRepository, MailService mailService) {
        this.memberRepository = memberRepository;
        this.mailService = mailService;
    }

    /**
     * Display the form to request the deletion of the account.
     *
     * @param model The model to be used in the view
     * @return The name of the view to be displayed
     */
    @GetMapping(value = "/requestDeleteAccount")
    public String requestDeleteAccountDisplayForm(Model model) {
        log.info("Call to requestDeleteAccountDisplayForm REST endpoint");
        model.addAttribute("deleteAccountRequest", new DeleteAccountRequest());
        return "request-delete-account";
    }

    /**
     * Process the deletion of the account request.
     *
     * @param deleteAccountRequest The request data containing the user's e-mail address, OTP, and reason
     * @param model                The model to be used in the view
     * @return The name of the view to be displayed
     */
    @PostMapping("/requestDeleteAccount")
    public String requestDeleteAccountSubmit(@ModelAttribute DeleteAccountRequest deleteAccountRequest, Model model) {
        log.info("Call to requestDeleteAccountSubmit REST endpoint");

        log.info("Email: {}", deleteAccountRequest.getEmail());
        log.info("OTP: {}", deleteAccountRequest.getOtp());
        log.info("Reason: {}", deleteAccountRequest.getReason());

        // e-mail address has not been specified
        if (deleteAccountRequest.getEmail() == null || deleteAccountRequest.getEmail().isEmpty()) {
            log.info("No e-mail address specified");
            model.addAttribute("result", "missing_email");
            return "request-delete-account-result";
        }

        // get the user
        final Optional<Member> member = memberRepository.findByEmailCustom(deleteAccountRequest.getEmail());

        // e-mail address does not exist
        if (member.isEmpty()) {
            log.info("E-mail address {} has not been found", deleteAccountRequest.getEmail());
            model.addAttribute("result", "member_not_found");
            return "request-delete-account-result";
        }

        // check OTP
        if (!Objects.equals(member.get().getOtp(), deleteAccountRequest.getOtp())) {
            log.info("Invalid OTP for e-mail address {}", deleteAccountRequest.getEmail());
            model.addAttribute("result", "invalid_otp");
            return "request-delete-account-result";
        }

        model.addAttribute("result", "success");
        return "request-delete-account-result";
    }

    /**
     * Send a one-time password to the specified e-mail address for delete account request.
     *
     * @param request The request data containing the user's e-mail address
     * @return An empty body response with one of the following HTTP status :
     * <ul>
     * <li>400 Bad request if e-mail address is missing from the request</li>
     * <li>404 Not Found if no account has been found for the specified e-mail address</li>
     * <li>207 Multi-status if the OTP has been successfully updated but the mail failed to be sent</li>
     * <li>200 Ok if OTP has been sent successfully</li>
     * </ul>
     */
    @PostMapping("/requestDeleteAccountOtp")
    public ResponseEntity<HttpStatus> requestDeleteAccountOtp(@RequestBody Map<String, String> request) {
        log.info("Call to requestDeleteAccountOtp REST endpoint");

        // e-mail address has not been specified
        if (request.get("email") == null || request.get("email").isEmpty()) {
            log.info("No e-mail address specified");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // get the user
        final Optional<Member> member = memberRepository.findByEmailCustom(request.get("email"));

        // e-mail address does not exist
        if (member.isEmpty()) {
            log.info("E-mail address {} has not been found", request.get("email"));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // generate a new OTP and save the user
        member.get().setOtp(ThreadLocalRandom.current().nextInt(1000, 10000) + "");
        member.get().setOtpDate(LocalDateTime.now(ZoneId.of("Europe/Paris")));
        memberRepository.save(member.get());
        log.info("Delete account OTP request has been generated and saved for e-mail address {}", request.get("email"));

        // send delete account OTP request e-mail
        try {
            mailService.sendDeleteAccountRequestOtpEmail(member.get());
            log.info("Delete account OTP request e-mail sent to {}", member.get().getEmail());
        } catch (Exception e) {
            log.error("Error while sending delete account OTP request e-mail to {}", member.get().getEmail(), e);
            return ResponseEntity.status(HttpStatus.MULTI_STATUS).build();
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}