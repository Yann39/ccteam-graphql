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

import com.ccteam.graphql.entities.Member;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Mail utilities.
 *
 * @author yann39
 * @since 1.0.0
 */
@Service
public class MailService {

    @Autowired
    private JavaMailSender sender;

    public void sendRegistrationEmail(Member member) throws Exception {
        final MimeMessage message = sender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setTo(member.getEmail());
        helper.setSubject("CCTeam - Confirmez votre adresse e-mail");

        String body = "<p>Bonjour " + member.getFirstName() + ",</p><br/>";
        body = body + "<p>Bienvenue sur l'application CCTeam !</p>";
        body = body + "<p>Veuillez saisir le code suivant dans l'application afin de finaliser votre inscription : <b>" + member.getOtp() + "</b></p>";
        body = body + "<p>Le code est valide <b>10</b> minutes.</p><br/>";
        body = body + "<p>L'équipe CCTeam</p>";

        helper.setText(body, true);

        sender.send(message);
    }

    public void sendForgotPasswordEmail(String email) throws Exception {
        final MimeMessage message = sender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setTo(email);
        helper.setSubject("CCTeam - Votre mot de passe");
        helper.setText("Bonjour");

        sender.send(message);
    }
}