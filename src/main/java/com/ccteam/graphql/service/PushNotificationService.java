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

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.util.Map;

/**
 * Push notification (Firebase Cloud Messaging) service.
 * <p>
 * Notifications are sent to FCM <b>topics</b>, the mobile application subscribes the device to the relevant topics
 * according to the login state and the logged member's event registrations. Nothing is stored server-side (no device
 * token management).
 * <p>
 * The service authenticates against Firebase with a service account key file whose path is set in the
 * {@code ct.firebase.service-account-file} property.
 *
 * @author yann39
 * @since 1.0.3
 */
@Service
@Slf4j
public class PushNotificationService {

    /**
     * Topic notified when a news is published. Must match the topic name subscribed by the mobile application.
     */
    public static final String TOPIC_NEWS = "news";

    /**
     * Prefix of the per-event topics ({@code event-{id}}), notified when the event is about to start. The mobile
     * application only subscribes the device to the topics of the upcoming events the logged member is registered to,
     * so only participants receive the reminder. Must match the prefix used by the mobile application.
     */
    public static final String TOPIC_EVENT_PREFIX = "event-";

    @Value("${ct.firebase.service-account-file:}")
    private String serviceAccountFile;

    /**
     * Whether the Firebase SDK was initialized successfully and notifications can be sent.
     */
    @Getter
    private boolean enabled = false;

    @PostConstruct
    void initialize() {
        if (serviceAccountFile == null || serviceAccountFile.isBlank()) {
            log.warn("No Firebase service account file configured (ct.firebase.service-account-file), push notifications are disabled");
            return;
        }
        try (FileInputStream credentials = new FileInputStream(serviceAccountFile)) {
            // guard against double initialization (e.g. context refresh in tests)
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(credentials))
                        // bounded timeouts so a FCM outage can't stall the calling operation (e.g. news creation) for long
                        .setConnectTimeout(5000)
                        .setReadTimeout(5000)
                        .build());
            }
            enabled = true;
            log.info("Firebase initialized from {}, push notifications are enabled", serviceAccountFile);
        } catch (Exception e) {
            log.error("Could not initialize Firebase from service account file {}, push notifications are disabled", serviceAccountFile, e);
        }
    }

    /**
     * Send a push notification to all devices subscribed to the given topic.
     * <p>
     * Failures are logged but never thrown, sending a notification is always a best-effort side effect that must not
     * break the calling business operation.
     *
     * @param topic The FCM topic to send to ({@link #TOPIC_NEWS}, or {@link #TOPIC_EVENT_PREFIX} + event id)
     * @param title The notification title
     * @param body  The notification body
     * @param data  Optional custom key/value pairs attached to the message (e.g. entity type and id)
     * @return {@code true} if the notification was accepted by FCM, {@code false} otherwise
     */
    public boolean sendToTopic(String topic, String title, String body, Map<String, String> data) {
        if (!enabled) {
            log.info("Push notifications are disabled, skipping notification '{}' to topic {}", title, topic);
            return false;
        }
        try {
            final Message message = Message.builder()
                    .setTopic(topic)
                    // high priority so delivery is immediate even when the device is idle (doze mode
                    // defers normal-priority messages, sometimes by a long time)
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .build())
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putAllData(data == null ? Map.of() : data)
                    .build();
            final String messageId = FirebaseMessaging.getInstance().send(message);
            log.info("Notification '{}' sent to topic {} ({})", title, topic, messageId);
            return true;
        } catch (Exception e) {
            log.error("Failed to send notification '{}' to topic {}", title, topic, e);
            return false;
        }
    }

}
