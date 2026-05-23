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

import com.ccteam.graphql.config.graphql.CustomGraphQLException;
import com.ccteam.graphql.entities.*;
import com.ccteam.graphql.enums.BoardRole;
import com.ccteam.graphql.repository.MemberRepository;
import com.ccteam.graphql.repository.MembershipFeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

/**
 * {@link News} service.
 *
 * @author yann39
 * @since 1.0.0
 */
@Service
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final MembershipFeeRepository membershipFeeRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, MembershipFeeRepository membershipFeeRepository,
                         PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.membershipFeeRepository = membershipFeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Get all members.
     *
     * @return A list of {@link Member} objects representing the members
     */
    public List<Member> getAllMembers() {
        return memberRepository.findAllCustom();
    }

    /**
     * Lightweight count of all members. Exposed at
     * the USER level so it can be accessible to non-MEMBER users.
     */
    public long getMembersCount() {
        return memberRepository.count();
    }

    /**
     * Get a member given its {@code id}.
     *
     * @param id The ID of the member to retrieve
     * @return A {@link Member} object representing the member
     */
    public Member getMemberById(Long id) {
        final Optional<Member> memberOptional = memberRepository.findByIdCustom(id);
        if (memberOptional.isEmpty()) {
            log.error("Member with id {} not found in the database", id);
            throw new CustomGraphQLException("member_not_found", "Specified member has not been found in the database");
        }
        return memberOptional.get();
    }

    /**
     * Get a member given its e-mail address.
     *
     * @param email The member's e-mail address
     * @return A {@link Member} object representing the member
     */
    public Member getMemberByEmail(String email) {
        final Optional<Member> memberOptional = memberRepository.findByEmailCustom(email);
        if (memberOptional.isEmpty()) {
            log.error("Member with e-mail {} not found in the database", email);
            throw new CustomGraphQLException("member_not_found", "Specified member has not been found in the database");
        }
        return memberOptional.get();
    }

    /**
     * Get all members according to the specified filter {@code text}.
     * <p>
     * Search is done on first name, last name and e-mail address. If {@code text} filter is null,
     * all records will be returned.
     *
     * @param text The text filter string
     * @return A list of {@link Member} objects representing the members
     */
    public List<Member> getMembersFiltered(String text) {
        return memberRepository.findFilteredCustom(text);
    }

    /**
     * Create a new member.
     *
     * @param firstName      The member first name
     * @param lastName       The member last name
     * @param email          The member e-mail address
     * @param phone          The member phone number
     * @param avatarFile     The member avatar file as base64 encoded string
     * @param avatarFileName The member avatar file name
     * @return A {@link Member} object representing the member just created
     */
    public Member createMember(String firstName, String lastName, String email, String phone, Integer riderNumber,
                               String avatarFile, String avatarFileName, Member.Role role) {

        final Optional<Member> memberOptional = memberRepository.findByEmailCustom(email);
        if (memberOptional.isPresent()) {
            log.error("Member with e-mail address {} already exist in the database", email);
            throw new CustomGraphQLException("member_email_already_exist",
                    "A member with the same e-mail address already exist in the database");
        }

        final Member member = new Member();
        member.setFirstName(firstName);
        member.setLastName(lastName);
        member.setEmail(email);
        member.setPhone(phone);
        member.setRiderNumber(riderNumber);
        member.setRegistrationDate(LocalDateTime.now());
        member.setCreatedOn(LocalDateTime.now());
        member.setRole(role);

        if (avatarFile != null) {
            final byte[] decoded = Base64.getDecoder().decode(avatarFile.getBytes());
            final Attachment attachment = new Attachment();
            attachment.setFilename(avatarFileName);
            attachment.setFile(decoded);
            attachment.setUploadDate(LocalDateTime.now());
            member.setAvatar(attachment);
        }

        return memberRepository.save(member);
    }

    /**
     * Update the member represented by the given member ID with the specified data.
     * <p>
     * Annotated {@code @Transactional} so the {@code findByIdCustom} (which eagerly join-fetches
     * {@code likedNews → news}, {@code bikes}, etc.) and the subsequent {@code save} share a single Hibernate session.
     * Without it, the detached entity returned by {@code save} carries uninitialised lazy proxies during GraphQL
     * response serialization.
     *
     * @param memberId       The ID of the {@link Member} to update
     * @param firstName      The member first name
     * @param lastName       The member last name
     * @param email          The member e-mail address
     * @param phone          The member phone number
     * @param avatarFile     The member avatar file as base64 encoded string
     * @param avatarFileName The member avatar file name
     * @return An {@link Event} object representing the event just updated
     */
    @Transactional
    public Member updateMember(long memberId, String firstName, String lastName, String email, String phone,
                               Integer riderNumber, String avatarFile, String avatarFileName, Member.Role role) {
        final Optional<Member> memberOptional = memberRepository.findByIdCustom(memberId);
        if (memberOptional.isEmpty()) {
            log.error("Member with id {} not found in the database", memberId);
            throw new CustomGraphQLException("member_not_found",
                    "Specified member ID has not been found in the database");
        }

        final Member member = memberOptional.get();
        member.setFirstName(firstName);
        member.setLastName(lastName);
        member.setEmail(email);
        member.setPhone(phone);
        member.setRiderNumber(riderNumber);
        member.setRole(role);
        member.setModifiedOn(LocalDateTime.now());

        if (avatarFile != null) {
            final byte[] decoded;
            try {
                decoded = Base64.getDecoder().decode(avatarFile);
            } catch (IllegalArgumentException e) {
                log.error("Member {} tried to update avatar with invalid base64 (length = {} chars) : {}",
                        memberId, avatarFile.length(), e.getMessage());
                throw new CustomGraphQLException("invalid_avatar", "The avatar file is not valid base64");
            }

            if (member.getAvatar() != null) {
                member.getAvatar().setFilename(avatarFileName);
                member.getAvatar().setFile(decoded);
                member.getAvatar().setUploadDate(LocalDateTime.now());
            } else {
                final Attachment attachment = new Attachment();
                attachment.setFilename(avatarFileName);
                attachment.setFile(decoded);
                attachment.setUploadDate(LocalDateTime.now());
                member.setAvatar(attachment);
            }
        } else {
            log.info("Member {} tried to update avatar with null file, skipping avatar change", memberId);
        }

        return memberRepository.save(member);
    }

    /**
     * Delete the member represented by the given member ID.
     *
     * @param memberId The ID of the {@link Member} to delete
     * @return A {@link Member} object representing the member just deleted
     */
    public Member deleteMember(long memberId) {
        final Optional<Member> memberOptional = memberRepository.findByIdCustom(memberId);
        if (memberOptional.isEmpty()) {
            log.error("Member with id {} not found in the database", memberId);
            throw new CustomGraphQLException("member_not_found",
                    "Specified member ID has not been found in the database");
        }

        final Member member = memberOptional.get();
        memberRepository.delete(member);
        return member;
    }

    /**
     * Assign (or clear) the executive board role of the given member.
     * <p>
     * If a non-null {@code boardRole} is provided, any other member previously holding that role is automatically
     * demoted (their {@code boardRole} is set to {@code null}) so that only one member at a time holds each board
     * position.
     *
     * @param memberId  The id of the member whose role is being set
     * @param boardRole The role to assign, or {@code null} to clear
     * @return the updated {@link Member}
     */
    @Transactional
    public Member setBoardRole(long memberId, BoardRole boardRole) {
        final Optional<Member> memberOptional = memberRepository.findByIdCustom(memberId);
        if (memberOptional.isEmpty()) {
            log.error("Member with id {} not found in the database", memberId);
            throw new CustomGraphQLException("member_not_found",
                    "Specified member ID has not been found in the database");
        }

        final Member member = memberOptional.get();

        if (boardRole != null) {
            // demote any other member that already holds the same role
            final List<Member> previousHolders =
                    memberRepository.findByBoardRoleAndIdNot(boardRole, memberId);
            for (Member previous : previousHolders) {
                previous.setBoardRole(null);
                memberRepository.save(previous);
            }
        }

        member.setBoardRole(boardRole);
        return memberRepository.save(member);
    }

    /**
     * Set the color palette index the member has chosen for their detail-page header background.
     * Passing {@code null} resets the choice (the client will fall back to the id-based default).
     *
     * @param memberId      ID of the member
     * @param headerPalette The index in the client-side palette list, or {@code null} to clear the choice
     * @return the updated {@link Member}
     */
    @Transactional
    public Member setMemberPalette(long memberId, Integer headerPalette) {
        final Optional<Member> memberOptional = memberRepository.findByIdCustom(memberId);
        if (memberOptional.isEmpty()) {
            log.error("Member with id {} not found in the database", memberId);
            throw new CustomGraphQLException("member_not_found", "Specified member ID has not been found in the database");
        }
        final Member member = memberOptional.get();
        member.setHeaderPalette(headerPalette);
        return memberRepository.save(member);
    }

    /**
     * Change the passcode (BCrypt-hashed "password") of the member identified by the given e-mail address.
     * <p>
     * The {@code currentPasscode} is verified against the stored hash, on mismatch  a {@code bad_credentials} error
     * is thrown so the client can display an inline error and keep the user on the form. The new passcode must be
     * exactly 6 digits and different from the current one.
     * <p>
     * The JWT is intentionally NOT invalidated, only the credential changes, the session continues seamlessly.
     *
     * @param email           The e-mail of the member changing their passcode (from the JWT principal)
     * @param currentPasscode The current passcode, to verify ownership
     * @param newPasscode     The new passcode (6 digits)
     * @return {@code true} on success
     */
    @Transactional
    public boolean changePasscode(String email, String currentPasscode, String newPasscode) {
        final Optional<Member> memberOptional = memberRepository.findByEmailCustom(email);
        if (memberOptional.isEmpty()) {
            log.error("Member with e-mail {} not found in the database", email);
            throw new CustomGraphQLException("member_not_found",
                    "Specified member has not been found in the database");
        }
        final Member member = memberOptional.get();

        // verify ownership: current passcode must match the stored hash
        if (member.getPassword() == null || !passwordEncoder.matches(currentPasscode, member.getPassword())) {
            log.info("Passcode change refused for {} : current passcode does not match", email);
            throw new CustomGraphQLException("bad_credentials",
                    "Current passcode does not match");
        }

        // validate new passcode shape (server-side, even though the client enforces it too)
        if (newPasscode == null || !newPasscode.matches("\\d{6}")) {
            log.info("Passcode change refused for {} : new passcode is not 6 digits", email);
            throw new CustomGraphQLException("invalid_passcode",
                    "New passcode must be exactly 6 digits");
        }

        // refuse a no-op change so the user gets a clear feedback instead of a silent success
        if (passwordEncoder.matches(newPasscode, member.getPassword())) {
            log.info("Passcode change refused for {} : new passcode is identical to the current one", email);
            throw new CustomGraphQLException("same_passcode",
                    "New passcode must be different from the current one");
        }

        member.setPassword(passwordEncoder.encode(newPasscode));
        member.setModifiedOn(LocalDateTime.now());
        memberRepository.save(member);
        log.info("Passcode successfully updated for {}", email);
        return true;
    }

    /**
     * Add membership fee for the given member.
     *
     * @param memberId The ID of the {@link Member} to add membership fee for
     * @param year     The membership fee year
     * @param amount   The membership fee amount
     * @param paid     A boolean indicating if the membership fee is paid
     * @return A {@link MembershipFee} object representing the membership fee just added
     */
    public MembershipFee addMembershipFee(Long memberId, Integer year, Float amount, boolean paid) {
        final Optional<Member> memberOptional = memberRepository.findByIdCustom(memberId);
        if (memberOptional.isEmpty()) {
            log.error("Member with id {} not found in the database", memberId);
            throw new CustomGraphQLException("member_not_found", "Specified member ID has not been found in the database");
        }

        final Optional<MembershipFee> feeOptional = membershipFeeRepository.findByMemberIdAndYear(memberId, year);
        if (feeOptional.isPresent()) {
            log.error("Membership fee for member {} and year {} already exists", memberId, year);
            throw new CustomGraphQLException("membership_fee_already_exists", "Membership fee for this member and year already exists");
        }

        final MembershipFee fee = new MembershipFee();
        fee.setMember(memberOptional.get());
        fee.setYear(year);
        fee.setAmount(amount);
        fee.setPaid(paid);
        fee.setCreatedOn(LocalDateTime.now());

        return membershipFeeRepository.save(fee);
    }

    /**
     * Update membership fee for the given member.
     *
     * @param feeId  The ID of the {@link MembershipFee} to update
     * @param year   The membership fee year
     * @param amount The membership fee amount
     * @param paid   A boolean indicating if the membership fee is paid
     * @return A {@link MembershipFee} object representing the membership fee just updated
     */
    public MembershipFee updateMembershipFee(Long feeId, Integer year, Float amount, boolean paid) {
        final Optional<MembershipFee> feeOptional = membershipFeeRepository.findById(feeId);
        if (feeOptional.isEmpty()) {
            log.error("MembershipFee with id {} not found in the database", feeId);
            throw new CustomGraphQLException("membership_fee_not_found", "Specified membership fee ID has not been found in the database");
        }

        final MembershipFee fee = feeOptional.get();
        fee.setYear(year);
        fee.setAmount(amount);
        fee.setPaid(paid);
        fee.setModifiedOn(LocalDateTime.now());

        return membershipFeeRepository.save(fee);
    }

    /**
     * Delete membership fee for the given member.
     *
     * @param feeId The ID of the {@link MembershipFee} to delete
     * @return A {@link MembershipFee} object representing the membership fee just deleted
     */
    public MembershipFee deleteMembershipFee(Long feeId) {
        final Optional<MembershipFee> feeOptional = membershipFeeRepository.findById(feeId);
        if (feeOptional.isEmpty()) {
            log.error("MembershipFee with id {} not found in the database", feeId);
            throw new CustomGraphQLException("membership_fee_not_found", "Specified membership fee ID has not been found in the database");
        }

        final MembershipFee fee = feeOptional.get();
        membershipFeeRepository.delete(fee);
        return fee;
    }

}