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

import com.ccteam.graphql.entities.Event;
import com.ccteam.graphql.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Scheduled job sending a push notification reminder for upcoming events.
 * <p>
 * Every run looks for events starting within the next 24 hours whose
 * reminder has not been sent yet, notifies the per-event FCM topic
 * ({@code event-{id}}, subscribed only by the devices of the members
 * registered to the event) and stamps {@link Event#getReminderSentOn()} so
 * each event is reminded exactly once. An event whose notification failed
 * (FCM unreachable, etc.) is not stamped and is therefore retried on the
 * next run, as long as it has not started yet.
 *
 * @author yann39
 * @since 1.0.3
 */
@Service
@Slf4j
public class EventReminderService {

    private static final DateTimeFormatter REMINDER_DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy 'à' HH:mm");

    private final EventRepository eventRepository;
    private final PushNotificationService pushNotificationService;

    public EventReminderService(EventRepository eventRepository, PushNotificationService pushNotificationService) {
        this.eventRepository = eventRepository;
        this.pushNotificationService = pushNotificationService;
    }

    /**
     * Send the reminder notification for every event starting in less than
     * 24 hours that has not been reminded yet, to the members registered to
     * that event (through the per-event topic). Runs every 10 minutes (the
     * reminder therefore fires at most 10 minutes after the event enters
     * its last 24 hours, which is plenty accurate for this use case).
     */
    @Scheduled(initialDelay = 1, fixedDelay = 10, timeUnit = TimeUnit.MINUTES)
    public void sendUpcomingEventReminders() {
        // when notifications are disabled, don't stamp anything so the
        // reminders still within their window fire once they get enabled
        if (!pushNotificationService.isEnabled()) {
            return;
        }

        final LocalDateTime now = LocalDateTime.now();
        final List<Event> events = eventRepository.findEventsNeedingReminder(now, now.plusHours(24));

        for (final Event event : events) {
            log.info("Sending 24h reminder for event {} ({})", event.getId(), event.getTitle());

            final String when = REMINDER_DATE_FORMAT.format(event.getStartDate());
            final String body = event.getTrack() != null && event.getTrack().getName() != null
                    ? "C'est bientôt ! Rendez-vous le " + when + " sur le circuit de " + event.getTrack().getName() + "."
                    : "C'est bientôt ! Rendez-vous le " + when + ".";

            final boolean sent = pushNotificationService.sendToTopic(
                    PushNotificationService.TOPIC_EVENT_PREFIX + event.getId(),
                    "Rappel : " + event.getTitle(),
                    body,
                    Map.of("type", "event", "id", String.valueOf(event.getId())));

            // only stamp on success so failed sends are retried on the next run
            if (sent) {
                event.setReminderSentOn(LocalDateTime.now());
                eventRepository.save(event);
            }
        }
    }

}
