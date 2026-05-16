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

package com.ccteam.graphql.controller.graphql;

import com.ccteam.graphql.config.graphql.CustomGraphQLException;
import com.ccteam.graphql.entities.Member;
import com.ccteam.graphql.entities.MembershipFee;
import com.ccteam.graphql.enums.BoardRole;
import com.ccteam.graphql.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

/**
 * {@link Member} GraphQL controller.
 *
 * @author yann39
 * @since 1.0.0
 */
@Controller
@Slf4j
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * Get all members.
     *
     * @return A list of {@link Member} objects representing the members
     */
    @PreAuthorize("hasRole('MEMBER')")
    @QueryMapping
    public List<Member> getAllMembers() {
        log.info("Received call to getAllMembers");
        return memberService.getAllMembers();
    }

    /**
     * Lightweight count of all members in the database. Exposed at
     * the USER level so it can be accessible to non-MEMBER users.
     */
    @PreAuthorize("hasRole('USER')")
    @QueryMapping
    public Long getMembersCount() {
        log.info("Received call to getMembersCount");
        return memberService.getMembersCount();
    }

    /**
     * Get a member given its {@code id}.
     *
     * @param id The ID of the member to retrieve
     * @return A {@link Member} object representing the member
     */
    @PreAuthorize("hasRole('MEMBER')")
    @QueryMapping
    public Member getMemberById(@Argument Long id) {
        log.info("Received call to getMemberById with parameters ID = {}", id);
        return memberService.getMemberById(id);
    }

    /**
     * Get a member given its e-mail address.
     *
     * @param email The member's e-mail address
     * @return A {@link Member} object representing the member
     */
    @PreAuthorize("hasRole('USER')")
    @QueryMapping
    public Member getMemberByEmail(@Argument String email) {
        log.info("Received call to getMemberByEmail with parameters email = {}", email);
        return memberService.getMemberByEmail(email);
    }

    /**
     * Get all members according to the specified filter {@code text}.<br/>
     * Search is done on first name, last name and e-mail address.<br/>
     * If {@code text} filter is null, all records will be returned.
     *
     * @param text The text filter string
     * @return A list of {@link Member} objects representing the members
     */
    @PreAuthorize("hasRole('MEMBER')")
    @QueryMapping
    public List<Member> getMembersFiltered(@Argument String text) {
        log.info("Received call to getMembersFiltered with parameters text = {}", text);
        return memberService.getMembersFiltered(text);
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
     * @param riderNumber    The member rider number
     * @param active         The member active status
     * @param role           The member role
     * @return A {@link Member} object representing the member just created
     */
    @PreAuthorize("hasRole('ADMIN')")
    @MutationMapping
    public Member createMember(@Argument String firstName,
                               @Argument String lastName,
                               @Argument String email,
                               @Argument String phone,
                               @Argument String avatarFile,
                               @Argument String avatarFileName,
                               @Argument Integer riderNumber,
                               @Argument boolean active,
                               @Argument Member.Role role) {
        log.info("Received call to createMember with parameters firstName = {}, lastName = {}, email = {}, phone = {}, riderNumber = {}, avatarFile = {}, avatarFileName = {}, active = {}, role = {}",
                firstName, lastName, email, phone, riderNumber, avatarFile, avatarFileName, active, role);
        return memberService.createMember(firstName, lastName, email, phone, riderNumber, avatarFile, avatarFileName,
                active, role);
    }

    /**
     * Update the member represented by the given member ID with the specified data.
     * <p>
     * Authorization model: any authenticated user can call this
     * endpoint, but only to edit their own profile — admins can edit
     * anyone. We lowered the role gate from {@code MEMBER} to
     * {@code USER} so a freshly-registered member (who is still
     * {@code ROLE_USER} until promoted) can set up their avatar,
     * palette and personal info without waiting for admin approval.
     * The self-vs-other check is enforced at runtime via
     * {@link #ensureCanEdit(long, Authentication)}.
     *
     * @param memberId       The ID of the {@link Member} to update
     * @param firstName      The member first name
     * @param lastName       The member last name
     * @param email          The member e-mail address
     * @param phone          The member phone number
     * @param avatarFile     The member avatar file as base64 encoded string
     * @param avatarFileName The member avatar file name
     * @param riderNumber    The member rider number
     * @param active         The member active status
     * @param role           The member role
     * @return An {@link Member} object representing the member just updated
     */
    @PreAuthorize("hasRole('USER')")
    @MutationMapping
    public Member updateMember(@Argument long memberId,
                               @Argument String firstName,
                               @Argument String lastName,
                               @Argument String email,
                               @Argument String phone,
                               @Argument String avatarFile,
                               @Argument String avatarFileName,
                               @Argument Integer riderNumber,
                               @Argument boolean active,
                               @Argument Member.Role role,
                               Authentication authentication) {
        // intentionally not logging the avatar file to keep the log readable
        log.info("Received call to updateMember with parameters memberId = {}, firstName = {}, lastName = {}, email = {}, phone = {}, riderNumber = {}, avatarFileName = {}, active = {}, role = {}",
                memberId, firstName, lastName, email, phone, riderNumber, avatarFileName, active, role);
        final Member target = ensureCanEdit(memberId, authentication);
        // privilege-escalation guard: non-admins can't bump their own
        // role (or anyone else's, defensively). The form's role
        // dropdown is already gated to admins on the client, so this
        // is purely a server-side guarantee against tampered requests.
        if (!isAdmin(authentication) && target.getRole() != role) {
            log.info("Caller {} tried to change role of member {} from {} to {} — refused",
                    authentication.getName(), memberId, target.getRole(), role);
            throw new CustomGraphQLException("forbidden",
                    "You cannot change your role");
        }
        return memberService.updateMember(memberId, firstName, lastName, email, phone, riderNumber, avatarFile,
                avatarFileName, active, role);
    }

    /**
     * Delete the member represented by the given event ID.
     *
     * @param memberId The ID of the {@link Event} to delete
     * @return A {@link Event} object representing the member just deleted
     */
    @PreAuthorize("hasRole('ADMIN')")
    @MutationMapping
    public Member deleteMember(@Argument long memberId) {
        log.info("Received call to deleteMember with parameters memberId = {}", memberId);
        return memberService.deleteMember(memberId);
    }

    /**
     * Assign (or clear) the executive board role of the given member.
     *
     * @param memberId  ID of the member whose board role is being set
     * @param boardRole the role to assign, or {@code null} to clear the current role
     * @return the updated {@link Member}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @MutationMapping
    public Member setBoardRole(@Argument long memberId, @Argument BoardRole boardRole) {
        log.info("Received call to setBoardRole with parameters memberId = {}, boardRole = {}",
                memberId, boardRole);
        return memberService.setBoardRole(memberId, boardRole);
    }

    /**
     * Set the color palette the member has chosen for their
     * detail-page header background.
     * <p>
     * Authorization model: any authenticated user can set their own
     * palette; an admin can set anyone's. We lowered the role gate
     * from {@code MEMBER} to {@code USER} so a freshly-registered
     * member can customize their header without waiting for admin
     * promotion. The self-vs-other check is enforced at runtime via
     * {@link #ensureCanEdit(long, Authentication)}.
     *
     * @param memberId      ID of the member whose palette is being set
     * @param headerPalette palette index, or {@code null} to clear
     * @return the updated {@link Member}
     */
    @PreAuthorize("hasRole('USER')")
    @MutationMapping
    public Member setMemberPalette(@Argument long memberId,
                                   @Argument Integer headerPalette,
                                   Authentication authentication) {
        log.info("Received call to setMemberPalette with parameters memberId = {}, headerPalette = {}",
                memberId, headerPalette);
        ensureCanEdit(memberId, authentication);
        return memberService.setMemberPalette(memberId, headerPalette);
    }

    /**
     * Authorization guard for "self-edit or admin" mutations.
     * <p>
     * Allows the call when the caller is an admin, or when the target
     * {@code memberId} resolves to a member whose e-mail matches the
     * caller's authenticated principal. Throws a {@code forbidden}
     * {@link CustomGraphQLException} otherwise so the client gets a
     * stable error code (already mapped on the Flutter side as
     * "permission denied").
     * <p>
     * Returns the fetched {@link Member} so callers that need to
     * inspect the target (e.g. for additional privilege-escalation
     * checks) can do so without a second DB read.
     *
     * @param memberId       the id of the member being edited
     * @param authentication the current Spring Security authentication
     * @return the target {@link Member}
     */
    private Member ensureCanEdit(long memberId, Authentication authentication) {
        final Member target = memberService.getMemberById(memberId);
        if (isAdmin(authentication)) return target;
        if (target.getEmail() == null
            || !target.getEmail().equalsIgnoreCase(authentication.getName())) {
            log.info("Caller {} tried to edit member {} ({}) — refused", authentication.getName(), memberId, target.getEmail());
            throw new CustomGraphQLException("forbidden",
                    "You can only modify your own profile");
        }
        return target;
    }

    /**
     * Returns whether the given authentication carries the
     * {@code ROLE_ADMIN} authority. Stays role-string based (rather
     * than depending on the role hierarchy) to keep the check
     * unambiguous: a {@code MEMBER} or {@code USER} is NOT admin for
     * the purpose of cross-member edits, regardless of any future
     * hierarchy tweaks.
     */
    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    /**
     * Change the passcode of the currently authenticated member.
     * <p>
     * The target member is taken from the {@link Principal} (the JWT subject)
     * rather than passed as an argument, a member can only change their own
     * passcode, never anyone else's. {@code currentPasscode} is verified
     * against the stored hash to prove ownership; mismatch returns
     * {@code bad_credentials}.
     *
     * @param currentPasscode the current 6-digit passcode (for verification)
     * @param newPasscode     the new 6-digit passcode
     * @param principal       the authenticated user's principal (e-mail)
     * @return {@code true} on success
     */
    @PreAuthorize("hasRole('USER')")
    @MutationMapping
    public Boolean changePasscode(@Argument String currentPasscode,
                                  @Argument String newPasscode,
                                  Principal principal) {
        // intentionally NOT logging the passcodes
        log.info("Received call to changePasscode for {}", principal.getName());
        return memberService.changePasscode(principal.getName(), currentPasscode, newPasscode);
    }

    /**
     * Get if this member has an avatar. Used by the client to decide whether
     * to fetch the avatar bytes via the REST endpoint {@code /avatars/{id}} or
     * render the default placeholder, without dragging the bytes through every
     * GraphQL response.
     */
    @PreAuthorize("hasRole('USER')")
    @SchemaMapping(typeName = "Member", field = "hasAvatar")
    public Boolean hasAvatar(Member member) {
        return member.getAvatar() != null;
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
    @PreAuthorize("hasRole('ADMIN')")
    @MutationMapping
    public MembershipFee addMembershipFee(@Argument Long memberId, @Argument Integer year, @Argument Float amount, @Argument boolean paid) {
        log.info("Received call to addMembershipFee with parameters memberId = {}, year = {}, amount = {}, paid = {}", memberId, year, amount, paid);
        return memberService.addMembershipFee(memberId, year, amount, paid);
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
    @PreAuthorize("hasRole('ADMIN')")
    @MutationMapping
    public MembershipFee updateMembershipFee(@Argument Long feeId, @Argument Integer year, @Argument Float amount, @Argument boolean paid) {
        log.info("Received call to updateMembershipFee with parameters feeId = {}, year = {}, amount = {}, paid = {}", feeId, year, amount, paid);
        return memberService.updateMembershipFee(feeId, year, amount, paid);
    }

    /**
     * Delete membership fee for the given member.
     *
     * @param feeId The ID of the {@link MembershipFee} to delete
     * @return A {@link MembershipFee} object representing the membership fee just deleted
     */
    @PreAuthorize("hasRole('ADMIN')")
    @MutationMapping
    public MembershipFee deleteMembershipFee(@Argument Long feeId) {
        log.info("Received call to deleteMembershipFee with parameters feeId = {}", feeId);
        return memberService.deleteMembershipFee(feeId);
    }

}