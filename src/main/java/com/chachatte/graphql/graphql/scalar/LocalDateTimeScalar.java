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

package com.chachatte.graphql.graphql.scalar;

import graphql.language.StringValue;
import graphql.schema.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * @author yann39
 * @since sept 2020
 */
@Component
public class LocalDateTimeScalar extends GraphQLScalarType {

    public LocalDateTimeScalar() {
        super("LocalDateTime", "LocalDateTime type", new Coercing<LocalDateTime, String>() {

            @Override
            public String serialize(final Object dataFetcherResult) {
                if (dataFetcherResult instanceof LocalDateTime) {
                    return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format((LocalDateTime) dataFetcherResult);
                } else {
                    throw new CoercingSerializeException("Expected a LocalDateTime object");
                }
            }

            @Override
            public LocalDateTime parseValue(final Object input) {
                try {
                    if (input instanceof String) {
                        return LocalDateTime.parse((String) input);
                    } else {
                        throw new CoercingParseValueException("Expected a String value");
                    }
                } catch (DateTimeParseException e) {
                    throw new CoercingParseValueException(input + " is not a valid date", e);
                }
            }

            @Override
            public LocalDateTime parseLiteral(final Object input) {
                if (input instanceof StringValue) {
                    try {
                        return LocalDateTime.parse(((StringValue) input).getValue());
                    } catch (DateTimeParseException e) {
                        throw new CoercingParseLiteralException(e);
                    }
                } else {
                    throw new CoercingParseLiteralException("Expected a String literal");
                }
            }
        });

    }

}