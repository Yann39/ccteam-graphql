/*
 * Copyright (c) 2020 by Yann39.
 *
 * This file is part of Chachatte Team application.
 *
 * Chachatte Team is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Chachatte Team is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Chachatte Team. If not, see <http://www.gnu.org/licenses/>.
 */

package com.chachatte.graphql.controller;

import com.chachatte.graphql.entities.Member;
import com.chachatte.graphql.entities.News;
import com.chachatte.graphql.model.*;
import com.chachatte.graphql.repository.MemberRepository;
import com.chachatte.graphql.service.MailService;
import com.chachatte.graphql.service.NewsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * REST registration controller.
 *
 * @author yann39
 * @since Nov 2020
 */
@RestController
@CrossOrigin
@Component
@Slf4j
public class AccountController {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private NewsService newsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Check the account associated to the specified e-mail address.<br/>
     * It returns a specific status code according to the account current status,
     * so that the client knows what to do with that account.
     *
     * @param checkAccountRequest The request data containing the account e-mail address
     * @return 400 Bad request if e-mail address is missing from the request<br/>
     * 404 Not Found if no account has been found for the specified e-mail address<br/>
     * 302 Found if account exists, OTP has been sent and is still valid<br/>
     * 417 Expectation Failed if account exists, OTP has been sent but is not valid anymore
     * 403 Forbidden if account exists but is not verified<br/>
     * 200 Ok if account has been found and is verified
     */
    @RequestMapping(value = "/checkAccount", method = RequestMethod.POST)
    public ResponseEntity<?> checkAccount(@RequestBody CheckAccountRequest checkAccountRequest) {

        log.info("Call to checkAccount REST endpoint");

        // e-mail address has not been specified
        if (checkAccountRequest.getEmail() == null || checkAccountRequest.getEmail().length() < 1) {
            log.info("No e-mail address specified");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // get any member with the specified e-mail address
        final Optional<Member> member = memberRepository.findByEmail(checkAccountRequest.getEmail());

        // e-mail address does not exist
        if (member.isEmpty()) {
            log.info("E-mail address " + checkAccountRequest.getEmail() + " has not been found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // account exist, OTP has been sent and is still valid
        if (!member.get().isVerified() && member.get().getOtp() != null && member.get().getOtpDate().isBefore(LocalDateTime.now(ZoneId.of("Europe/Paris")).plusMinutes(10))) {
            log.info("Account with e-mail address " + checkAccountRequest.getEmail() + " exist, OTP has been sent and is still valid");
            return ResponseEntity.status(HttpStatus.FOUND).build();
        }

        // account exist, OTP has been sent but is not valid anymore
        if (!member.get().isVerified() && member.get().getOtp() != null && member.get().getOtpDate().isAfter(LocalDateTime.now(ZoneId.of("Europe/Paris")).plusMinutes(10))) {
            log.info("Account with e-mail address " + checkAccountRequest.getEmail() + " exist, OTP has been sent but is not valid anymore");
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }

        // account exist, OTP has been verified, but password has not been created
        if (member.get().isVerified() && member.get().getPassword() == null) {
            log.info("Account with e-mail address " + checkAccountRequest.getEmail() + " exist but is not verified");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        log.info("Account with e-mail address " + checkAccountRequest.getEmail() + " exist and has a password");
        return ResponseEntity.status(HttpStatus.OK).build();

    }

    /**
     * Pre-register a new user given its e-mail address, first name and last name.<br/>
     * It creates the account with minimal information, but the user will still need to
     * confirm its e-mail address and create a passcode to complete the registration process.
     *
     * @param preRegisterRequest The request data containing the user's e-mail address, first name and last name
     * @return 400 Bad request if e-mail address, first name, or last name is missing from the request<br/>
     * 409 Conflict if a user already exist with the same e-mail address<br/>
     * 207 Multi-status if the user is successfully created but the confirmation e-mail failed to be sent
     * 201 Created if succeeded
     */
    @RequestMapping(value = "/preRegister", method = RequestMethod.POST)
    public ResponseEntity<?> preRegister(@RequestBody PreRegisterRequest preRegisterRequest) {

        log.info("Call to preRegister REST endpoint");

        // e-mail address has not been specified
        if (preRegisterRequest.getEmail() == null || preRegisterRequest.getEmail().length() < 1) {
            log.info("No e-mail address specified");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // first name has not been specified
        if (preRegisterRequest.getFirstName() == null || preRegisterRequest.getFirstName().length() < 1) {
            log.info("No first name specified");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // last name has not been specified
        if (preRegisterRequest.getLastName() == null || preRegisterRequest.getLastName().length() < 1) {
            log.info("No last name address specified");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // e-mail address already exists
        if (memberRepository.existsMemberByEmail(preRegisterRequest.getEmail())) {
            log.info("A member already exist with the e-mail address " + preRegisterRequest.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // create the user
        final Member member = new Member();
        member.setFirstName(preRegisterRequest.getFirstName());
        member.setLastName(preRegisterRequest.getLastName());
        member.setEmail(preRegisterRequest.getEmail());
        member.setCreatedOn(LocalDateTime.now(ZoneId.of("Europe/Paris")));
        member.setOtp(ThreadLocalRandom.current().nextInt(1000, 10000) + "");
        member.setOtpDate(LocalDateTime.now(ZoneId.of("Europe/Paris")));
        memberRepository.save(member);

        // send registration e-mail
        try {
            mailService.sendRegistrationEmail(member);
            log.info("Registration e-mail sent to " + member.getEmail());
        } catch (Exception ex) {
            log.error("Error while sending registration email to " + member.getEmail() + " : " + ex);
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.MULTI_STATUS).build();
        }

        log.info("Pre-registration done for user " + member.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    /**
     * Send a new one-time password to the specified e-mail address.<br/>
     * It is used in case user has not entered the OTP in the given time, or if he manually ask a new OTP.
     *
     * @param resendOtpRequest The request data containing the user's e-mail address
     * @return 400 Bad request if e-mail address is missing from the request<br/>
     * 404 Not Found if no account has been found for the specified e-mail address<br/>
     * 207 Multi-status if the OTP has been successfully updated but the mail failed to be sent
     * 200 Ok if OTP has been resent successfully
     */
    @RequestMapping(value = "/resendOtp", method = RequestMethod.POST)
    public ResponseEntity<?> resendOtp(@RequestBody ResendOtpRequest resendOtpRequest) {

        log.info("Call to resendOtp REST endpoint");

        // e-mail address has not been specified
        if (resendOtpRequest.getEmail() == null || resendOtpRequest.getEmail().length() < 1) {
            log.info("No e-mail address specified");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // get the user
        final Optional<Member> member = memberRepository.findByEmail(resendOtpRequest.getEmail());

        // e-mail address does not exist
        if (member.isEmpty()) {
            log.info("E-mail address " + resendOtpRequest.getEmail() + " has not been found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // generate a new OTP and save the user
        member.get().setOtp(ThreadLocalRandom.current().nextInt(1000, 10000) + "");
        member.get().setOtpDate(LocalDateTime.now(ZoneId.of("Europe/Paris")));
        memberRepository.save(member.get());
        log.info("New OTP has been generated and saved for e-mail address " + resendOtpRequest.getEmail());

        // send registration e-mail
        try {
            mailService.sendRegistrationEmail(member.get());
            log.info("Registration e-mail resent to " + member.get().getEmail());
        } catch (Exception ex) {
            log.error("Error while resending registration email to " + member.get().getEmail() + " : " + ex);
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.MULTI_STATUS).build();
        }

        return ResponseEntity.status(HttpStatus.OK).build();

    }

    /**
     * Confirm the specified e-mail address by checking the specified one-time password which was sent on registration.
     *
     * @param confirmEmailRequest The request data containing the user's e-mail address and the OTP to be confirmed
     * @return 400 Bad request if e-mail address or OTP is missing from the request<br/>
     * 404 Not found if the specified user's e-mail address has not been found in the database<br/>
     * 406 Not acceptable if the specified OTP has expired<br/>
     * 401 Unauthorized if the specified OTP does not match the one from the database<br/>
     * 202 Accepted if e-mail has been verified successfully
     */
    @RequestMapping(value = "/confirmEmail", method = RequestMethod.POST)
    public ResponseEntity<?> confirmEmail(@RequestBody ConfirmEmailRequest confirmEmailRequest) {

        log.info("Call to confirmEmail REST endpoint");

        // e-mail address has not been specified
        if (confirmEmailRequest.getEmail() == null || confirmEmailRequest.getEmail().length() < 1) {
            log.info("No e-mail address specified");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // OTP code has not been specified
        if (confirmEmailRequest.getOtp() == null || confirmEmailRequest.getOtp().length() < 1) {
            log.info("No OTP code specified");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // get member from the database given the specified e-mail address
        final Optional<Member> optMember = memberRepository.findByEmail(confirmEmailRequest.getEmail());

        // member not found in the database
        if (optMember.isEmpty()) {
            log.info("No member found in the database with address " + confirmEmailRequest.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        final Member member = optMember.get();

        // specified OTP has expired
        if (member.getOtpDate().isAfter(LocalDateTime.now(ZoneId.of("Europe/Paris")).plusMinutes(10))) {
            log.info("Specified OTP has expired");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }

        // specified OTP does not match the one from the database
        if (!member.getOtp().equalsIgnoreCase(confirmEmailRequest.getOtp())) {
            log.info("Specified OTP " + confirmEmailRequest.getOtp() + " does not match the one from the database");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // remove OTP data and activate member
        member.setOtp(null);
        member.setOtpDate(null);
        member.setVerified(true);
        memberRepository.save(member);

        log.info("E-mail address confirmed for user " + member.getEmail());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();

    }

    /**
     * Complete the registration for the specified account, especially by setting the specified password.
     *
     * @param completeRegistrationRequest The request data containing the user's e-mail address and the password to be defined
     * @return 400 Bad request if e-mail address or password is missing<br/>
     * 404 Not found if the specified user's e-mail address is not found in the database<br/>
     * 200 Ok if succeeded
     */
    @RequestMapping(value = "/completeRegistration", method = RequestMethod.POST)
    public ResponseEntity<?> completeRegistration(@RequestBody CompleteRegistrationRequest completeRegistrationRequest) {

        // e-mail address has not been specified
        if (completeRegistrationRequest.getEmail() == null || completeRegistrationRequest.getEmail().length() < 1) {
            log.info("No e-mail address specified");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // password has not been specified
        if (completeRegistrationRequest.getPassword() == null || completeRegistrationRequest.getPassword().length() < 1) {
            log.info("No password specified");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // get member from the database given the specified e-mail address
        final Optional<Member> optMember = memberRepository.findByEmail(completeRegistrationRequest.getEmail());

        // member not found in the database
        if (optMember.isEmpty()) {
            log.info("No member found in the database with address " + completeRegistrationRequest.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        final Member member = optMember.get();

        // encode and store password
        member.setPassword(passwordEncoder.encode(completeRegistrationRequest.getPassword()));
        member.setRole(Member.Role.ROLE_USER);
        member.setActive(true);
        member.setRegistrationDate(LocalDateTime.now(ZoneId.of("Europe/Paris")));
        memberRepository.save(member);

        log.info("Registration completed for member " + member.getEmail());
        return ResponseEntity.status(HttpStatus.OK).build();

    }

    /**
     * Forgot password.
     *
     * @param preRegisterRequest The request data containing the user's e-mail address
     * @return 400 Bad request if e-mail address is missing<br/>
     * 404 Not found if the specified user's e-mail address is not found in the database<br/>
     * 200 Ok if succeeded
     */
    @RequestMapping(value = "/forgotPassword", method = RequestMethod.POST)
    public ResponseEntity<?> forgotPassword(@RequestBody PreRegisterRequest preRegisterRequest) {

        // e-mail address has not been specified
        if (preRegisterRequest.getEmail() == null || preRegisterRequest.getEmail().length() < 1) {
            log.info("No e-mail address specified");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // get member from the database given the specified e-mail address
        final Optional<Member> optMember = memberRepository.findByEmail(preRegisterRequest.getEmail());

        // member not found in the database
        if (optMember.isEmpty()) {
            log.info("No member found in the database with address " + preRegisterRequest.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        final Member member = optMember.get();

        // generate a random string for OTP
        final byte[] array = new byte[8];
        new Random().nextBytes(array);
        final String otp = new String(array, StandardCharsets.UTF_8);

        // set new OTP
        member.setOtp(otp);
        member.setOtpDate(LocalDateTime.now(ZoneId.of("Europe/Paris")));
        memberRepository.save(member);

        // send forgot password e-mail
        try {
            mailService.sendForgotPasswordEmail(member.getEmail());
            log.info("Forgot password e-mail sent to " + member.getEmail());
        } catch (Exception ex) {
            log.error("Error while sending forgot password email to " + member.getEmail() + " : " + ex);
            ex.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.OK).build();

    }

    @RequestMapping(value = "/news", method = RequestMethod.POST)
    public ResponseEntity<?> listNews(@RequestBody PreRegisterRequest preRegisterRequest) {
        log.info("OK in news");

        for (News news : newsService.getNewsFilteredPaginated(preRegisterRequest.getEmail(), 0, 10, "title", "asc")) {
            log.info(news.getTitle());
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}