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
import com.ccteam.graphql.entities.EventReminder;
import com.ccteam.graphql.enums.ReminderOffset;
import com.ccteam.graphql.repository.EventReminderRepository;
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
 * Scheduled job sending push notification reminders for upcoming events.
 * <p>
 * Every run goes through the {@link ReminderOffset} catalog: for each offset, the events starting within that
 * delay whose (event, offset) reminder has not been sent yet are notified on their per-offset FCM topic
 * ({@code event-{id}-{offsetKey}}). Devices only receive the reminders whose offset the user selected in the
 * notification settings, since the mobile application only subscribes to those topics.
 * <p>
 * Each sent reminder is stamped in the {@code event_reminder} table so it is sent exactly once. A reminder whose
 * notification failed (FCM unreachable, etc.) is not stamped and is therefore retried on the next run, as long as
 * the event has not started yet.
 *
 * @author yann39
 * @since 1.0.3
 */
@Service
@Slf4j
public class EventReminderService {

    private static final DateTimeFormatter REMINDER_DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy 'à' HH:mm");

    private final EventRepository eventRepository;
    private final EventReminderRepository eventReminderRepository;
    private final PushNotificationService pushNotificationService;

    public EventReminderService(EventRepository eventRepository,
                                EventReminderRepository eventReminderRepository,
                                PushNotificationService pushNotificationService) {
        this.eventRepository = eventRepository;
        this.eventReminderRepository = eventReminderRepository;
        this.pushNotificationService = pushNotificationService;
    }

    /**
     * Send the due reminder notifications for every event and every reminder offset of the catalog.
     * Runs every minute, so a reminder fires at most one minute after the event enters the offset's window.
     */
    @Scheduled(initialDelay = 1, fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void sendUpcomingEventReminders() {
        // when notifications are disabled, don't stamp anything so the
        // reminders still within their window fire once they get enabled
        if (!pushNotificationService.isEnabled()) {
            return;
        }

        final LocalDateTime now = LocalDateTime.now();

        for (final ReminderOffset offset : ReminderOffset.values()) {
            final List<Event> events = eventRepository.findEventsStartingBetween(now, now.plus(offset.getDuration()));

            for (final Event event : events) {
                if (eventReminderRepository.existsByEventIdAndOffsetKey(event.getId(), offset.getKey())) {
                    continue;
                }

                log.info("Sending {} reminder for event {} ({})", offset.getKey(), event.getId(), event.getTitle());

                final String when = REMINDER_DATE_FORMAT.format(event.getStartDate());
                final String body = event.getTrack() != null && event.getTrack().getName() != null
                        ? "C'est bientôt ! Rendez-vous le " + when + " sur le circuit " + event.getTrack().getName() + "."
                        : "C'est bientôt ! Rendez-vous le " + when + ".";

                final boolean sent = pushNotificationService.sendToTopic(
                        PushNotificationService.TOPIC_EVENT_PREFIX + event.getId() + "-" + offset.getKey(),
                        "Rappel : " + event.getTitle(),
                        body,
                        Map.of("type", "event", "id", String.valueOf(event.getId())));

                // only stamp on success so failed sends are retried on the next run
                if (sent) {
                    final EventReminder reminder = new EventReminder();
                    reminder.setEventId(event.getId());
                    reminder.setOffsetKey(offset.getKey());
                    reminder.setSentOn(LocalDateTime.now());
                    eventReminderRepository.save(reminder);
                }
            }
        }
    }

}
