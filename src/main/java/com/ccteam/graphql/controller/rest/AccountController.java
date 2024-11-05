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
import com.ccteam.graphql.model.*;
import com.ccteam.graphql.repository.MemberRepository;
import com.ccteam.graphql.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

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
public class AccountController {

    public static final String ZONE_ID_EUROPE_PARIS = "Europe/Paris";

    private final MemberRepository memberRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    public AccountController(MemberRepository memberRepository, MailService mailService, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Check the account associated to the specified e-mail address.
     * <p>
     * It returns a specific status code according to the account current status,
     * so that the client knows what to do with that account.
     *
     * @param checkAccountRequest The request data containing the account e-mail address
     * @return An empty body response with one of the following HTTP status :
     * <ul>
     * <li>400 Bad request if e-mail address is missing from the request</li>
     * <li>404 Not Found if no account has been found for the specified e-mail address</li>
     * <li>302 Found if account exists, OTP has been sent and is still valid</li>
     * <li>417 Expectation Failed if account exists, OTP has been sent but is not valid anymore</li>
     * <li>403 Forbidden if account exists but is not verified</li>
     * <li>200 Ok if account has been found and is verified</li>
     * </ul>
     */
    @PostMapping("/rest/checkAccount")
    public ResponseEntity<HttpStatus> checkAccount(@RequestBody CheckAccountRequest checkAccountRequest) {

        log.info("Call to checkAccount REST endpoint");

        // e-mail address has not been specified
        if (checkAccountRequest.getEmail() == null || checkAccountRequest.getEmail().isEmpty()) {
            log.info("No e-mail address specified");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // get any member with the specified e-mail address
        final Optional<Member> member = memberRepository.findByEmailCustom(checkAccountRequest.getEmail());

        // e-mail address does not exist
        if (member.isEmpty()) {
            log.info("E-mail address {} has not been found", checkAccountRequest.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // account exist, OTP has been sent and is still valid
        if (!member.get().isVerified() && member.get().getOtp() != null && member.get().getOtpDate().isBefore(LocalDateTime.now(ZoneId.of(ZONE_ID_EUROPE_PARIS)).plusMinutes(10))) {
            log.info("Account with e-mail address {} exist, OTP has been sent and is still valid", checkAccountRequest.getEmail());
            return ResponseEntity.status(HttpStatus.FOUND).build();
        }

        // account exist, OTP has been sent but is not valid anymore
        if (!member.get().isVerified() && member.get().getOtp() != null && member.get().getOtpDate().isAfter(LocalDateTime.now(ZoneId.of(ZONE_ID_EUROPE_PARIS)).plusMinutes(10))) {
            log.info("Account with e-mail address {} exist, OTP has been sent but is not valid anymore", checkAccountRequest.getEmail());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }

        // account exist, OTP has been verified, but password has not been created
        if (member.get().isVerified() && member.get().getPassword() == null) {
            log.info("Account with e-mail address {} exist but is not verified", checkAccountRequest.getEmail());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        log.info("Account with e-mail address {} exist and has a password", checkAccountRequest.getEmail());
        return ResponseEntity.status(HttpStatus.OK).build();

    }

    /**
     * Pre-register a new user given its e-mail address, first name and last name.
     * <p>
     * It creates the account with minimal information, but the user will still need to
     * confirm its e-mail address and create a passcode to complete the registration process.
     *
     * @param preRegisterRequest The request data containing the user's e-mail address, first name and last name
     * @return An empty body response with one of the following HTTP status :
     * <ul>
     * <li>400 Bad request if e-mail address, first name, or last name is missing from the request</li>
     * <li>409 Conflict if a user already exist with the same e-mail address</li>
     * <li>207 Multi-status if the user is successfully created but the confirmation e-mail failed to be sent</li>
     * <li>201 Created if succeeded</li>
     * </ul>
     */

    @PostMapping("/rest/preRegister")
    public ResponseEntity<HttpStatus> preRegister(@RequestBody PreRegisterRequest preRegisterRequest) {

        log.info("Call to preRegister REST endpoint");

        // e-mail address has not been specified
        if (preRegisterRequest.getEmail() == null || preRegisterRequest.getEmail().isEmpty()) {
            log.info("No e-mail address specified");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // first name has not been specified
        if (preRegisterRequest.getFirstName() == null || preRegisterRequest.getFirstName().isEmpty()) {
            log.info("No first name specified");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // last name has not been specified
        if (preRegisterRequest.getLastName() == null || preRegisterRequest.getLastName().isEmpty()) {
            log.info("No last name specified");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // e-mail address already exists
        if (memberRepository.existsMemberByEmail(preRegisterRequest.getEmail())) {
            log.info("A member already exist with the e-mail address {}", preRegisterRequest.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // create the user
        final Member member = new Member();
        member.setFirstName(preRegisterRequest.getFirstName());
        member.setLastName(preRegisterRequest.getLastName());
        member.setEmail(preRegisterRequest.getEmail());
        member.setCreatedOn(LocalDateTime.now(ZoneId.of(ZONE_ID_EUROPE_PARIS)));
        member.setOtp(String.valueOf(ThreadLocalRandom.current().nextInt(9999)));
        member.setOtpDate(LocalDateTime.now(ZoneId.of(ZONE_ID_EUROPE_PARIS)));
        member.setRole(Member.Role.ROLE_USER);
        memberRepository.save(member);

        // send registration e-mail
        try {
            mailService.sendRegistrationEmail(member);
            log.info("Registration e-mail sent to {}", member.getEmail());
        } catch (Exception e) {
            log.error("Error while sending registration e-mail to {}", member.getEmail(), e);
            return ResponseEntity.status(HttpStatus.MULTI_STATUS).build();
        }

        log.info("Pre-registration done for user {}", member.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    /**
     * Send a new one-time password to the specified e-mail address.
     * <p>
     * It is used in case user has not entered the OTP in the given time, or if he manually asks a new OTP.
     *
     * @param resendOtpRequest The request data containing the user's e-mail address
     * @return An empty body response with one of the following HTTP status :
     * <ul>
     * <li>400 Bad request if e-mail address is missing from the request</li>
     * <li>404 Not Found if no account has been found for the specified e-mail address</li>
     * <li>207 Multi-status if the OTP has been successfully updated but the mail failed to be sent</li>
     * <li>200 Ok if OTP has been resent successfully</li>
     * </ul>
     */
    @PostMapping("/rest/resendOtp")
    public ResponseEntity<HttpStatus> resendOtp(@RequestBody ResendOtpRequest resendOtpRequest) {

        log.info("Call to resendOtp REST endpoint");

        // e-mail address has not been specified
        if (resendOtpRequest.getEmail() == null || resendOtpRequest.getEmail().isEmpty()) {
            log.info("No e-mail address specified");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // get the user
        final Optional<Member> member = memberRepository.findByEmailCustom(resendOtpRequest.getEmail());

        // e-mail address does not exist
        if (member.isEmpty()) {
            log.info("E-mail address {} has not been found", resendOtpRequest.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // generate a new OTP and save the user
        member.get().setOtp(ThreadLocalRandom.current().nextInt(1000, 10000) + "");
        member.get().setOtpDate(LocalDateTime.now(ZoneId.of(ZONE_ID_EUROPE_PARIS)));
        memberRepository.save(member.get());
        log.info("New OTP has been generated and saved for e-mail address {}", resendOtpRequest.getEmail());

        // send registration e-mail
        try {
            mailService.sendRegistrationEmail(member.get());
            log.info("Registration e-mail resent to {}", member.get().getEmail());
        } catch (Exception e) {
            log.error("Error while resending registration e-mail to {}", member.get().getEmail(), e);
            return ResponseEntity.status(HttpStatus.MULTI_STATUS).build();
        }

        return ResponseEntity.status(HttpStatus.OK).build();

    }

    /**
     * Confirm the specified e-mail address by checking the specified one-time password which was sent on registration.
     *
     * @param confirmEmailRequest The request data containing the user's e-mail address and the OTP to be confirmed
     * @return An empty body response with one of the following HTTP status :
     * <ul>
     * <li>400 Bad request if e-mail address or OTP is missing from the request</li>
     * <li>404 Not found if the specified user's e-mail address has not been found in the database</li>
     * <li>406 Not acceptable if the specified OTP has expired</li>
     * <li>401 Unauthorized if the specified OTP does not match the one from the database</li>
     * <li>202 Accepted if e-mail has been verified successfully</li>
     * </ul>
     */
    @PostMapping("/rest/confirmEmail")
    public ResponseEntity<HttpStatus> confirmEmail(@RequestBody ConfirmEmailRequest confirmEmailRequest) {

        log.info("Call to confirmEmail REST endpoint");

        // e-mail address has not been specified
        if (confirmEmailRequest.getEmail() == null || confirmEmailRequest.getEmail().isEmpty()) {
            log.info("No e-mail address specified");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // OTP code has not been specified
        if (confirmEmailRequest.getOtp() == null || confirmEmailRequest.getOtp().isEmpty()) {
            log.info("No OTP code specified");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // get member from the database given the specified e-mail address
        final Optional<Member> optMember = memberRepository.findByEmailCustom(confirmEmailRequest.getEmail());

        // member not found in the database
        if (optMember.isEmpty()) {
            log.info("No member found in the database with e-mail address {}", confirmEmailRequest.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        final Member member = optMember.get();

        // specified OTP has expired
        if (member.getOtpDate().isAfter(LocalDateTime.now(ZoneId.of(ZONE_ID_EUROPE_PARIS)).plusMinutes(10))) {
            log.info("Specified OTP has expired");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }

        // specified OTP does not match the one from the database
        if (!member.getOtp().equalsIgnoreCase(confirmEmailRequest.getOtp())) {
            log.info("Specified OTP {} does not match the one from the database", confirmEmailRequest.getOtp());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // remove OTP data and activate member
        member.setOtp(null);
        member.setOtpDate(null);
        member.setVerified(true);
        memberRepository.save(member);

        log.info("E-mail address confirmed for user {}", member.getEmail());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();

    }

    /**
     * Complete the registration for the specified account, especially by setting the specified password.
     *
     * @param completeRegistrationRequest The request data containing the user's e-mail address and the password to be defined
     * @return An empty body response with one of the following HTTP status :
     * <ul>
     * <li>400 Bad request if e-mail address or password is missing</li>
     * <li>404 Not found if the specified user's e-mail address is not found in the database</li>
     * <li>200 Ok if succeeded</li>
     * </ul>
     */
    @PostMapping("/rest/completeRegistration")
    public ResponseEntity<HttpStatus> completeRegistration(@RequestBody CompleteRegistrationRequest completeRegistrationRequest) {

        // e-mail address has not been specified
        if (completeRegistrationRequest.getEmail() == null || completeRegistrationRequest.getEmail().isEmpty()) {
            log.info("No e-mail address specified");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // password has not been specified
        if (completeRegistrationRequest.getPassword() == null || completeRegistrationRequest.getPassword().isEmpty()) {
            log.info("No password specified");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // get member from the database given the specified e-mail address
        final Optional<Member> optMember = memberRepository.findByEmailCustom(completeRegistrationRequest.getEmail());

        // member not found in the database
        if (optMember.isEmpty()) {
            log.info("No member found in the database with e-mail address {}", completeRegistrationRequest.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        final Member member = optMember.get();

        // encode and store password
        member.setPassword(passwordEncoder.encode(completeRegistrationRequest.getPassword()));
        member.setRole(Member.Role.ROLE_USER);
        member.setActive(true);
        member.setRegistrationDate(LocalDateTime.now(ZoneId.of(ZONE_ID_EUROPE_PARIS)));
        memberRepository.save(member);

        log.info("Registration completed for member {}", member.getEmail());
        return ResponseEntity.status(HttpStatus.OK).build();

    }

    /**
     * Send a link to the specified user's e-mail address, so he can request a new password.
     *
     * @param forgotPasswordRequest The request data containing the user's e-mail address
     * @return An empty body response with one of the following HTTP status :
     * <ul>
     * <li>400 Bad request if e-mail address is missing</li>
     * <li>404 Not found if the specified user's e-mail address is not found in the database</li>
     * <li>207 Multi-status if the user has been found but the confirmation e-mail failed to be sent</li>
     * <li>200 Ok if succeeded</li>
     * </ul>
     */
    @PostMapping("/rest/forgotPassword")
    public ResponseEntity<HttpStatus> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {

        // e-mail address has not been specified
        if (forgotPasswordRequest.getEmail() == null || forgotPasswordRequest.getEmail().isEmpty()) {
            log.info("No e-mail address specified");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // get member from the database given the specified e-mail address
        final Optional<Member> optMember = memberRepository.findByEmailCustom(forgotPasswordRequest.getEmail());

        // member not found in the database
        if (optMember.isEmpty()) {
            log.info("No member found in the database with e-mail address {}", forgotPasswordRequest.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        final Member member = optMember.get();

        // set new OTP
        member.setOtp(String.valueOf(ThreadLocalRandom.current().nextInt(9999)));
        member.setOtpDate(LocalDateTime.now(ZoneId.of(ZONE_ID_EUROPE_PARIS)));
        memberRepository.save(member);

        // send forgot password e-mail
        try {
            mailService.sendForgotPasswordEmail(member.getEmail());
            log.info("Forgot password e-mail sent to {}", member.getEmail());
        } catch (Exception e) {
            log.error("Error while sending forgot password e-mail to {}", member.getEmail(), e);
            return ResponseEntity.status(HttpStatus.MULTI_STATUS).build();
        }

        return ResponseEntity.status(HttpStatus.OK).build();

    }

}