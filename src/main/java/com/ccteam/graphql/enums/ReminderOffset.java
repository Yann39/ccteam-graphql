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

package com.ccteam.graphql.enums;

import java.time.Duration;

/**
 * Catalog of the delays before an event's start at which a reminder push
 * notification is sent.
 * <p>
 * Each (event, offset) pair has its own FCM topic ({@code event-{id}-{key}}).
 * The backend sends every offset of every event; the mobile application only
 * subscribes the device to the offsets selected by the user in the
 * notification settings, so the filtering happens at subscription time and
 * the backend does not need to know any user preference. The keys must match
 * the ones used by the mobile application.
 *
 * @author yann39
 * @since 1.0.2
 */
public enum ReminderOffset {

    ONE_HOUR("1h", Duration.ofHours(1)),
    TWELVE_HOURS("12h", Duration.ofHours(12)),
    ONE_DAY("1d", Duration.ofDays(1)),
    TWO_DAYS("2d", Duration.ofDays(2)),
    ONE_WEEK("1w", Duration.ofDays(7));

    private final String key;
    private final Duration duration;

    ReminderOffset(String key, Duration duration) {
        this.key = key;
        this.duration = duration;
    }

    public String getKey() {
        return key;
    }

    public Duration getDuration() {
        return duration;
    }

}
