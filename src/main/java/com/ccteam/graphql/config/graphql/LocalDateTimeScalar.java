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

package com.ccteam.graphql.config.graphql;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.*;
import graphql.schema.idl.RuntimeWiring;
import jakarta.annotation.Nonnull;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Custom GraphQL scalar implementation of the {@link LocalDateTime} Java type.
 * <p>
 * It can parse a GraphQL {@link StringValue} object to a Java {@link LocalDateTime} object,
 * and serialize a {@link LocalDateTime} object as {@link String} in the format <i>yyyy-MM-dd HH:mm:ss</i>
 *
 * @author yann39
 * @since 1.0.0
 */
@Configuration
public class LocalDateTimeScalar implements RuntimeWiringConfigurer {

    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void configure(RuntimeWiring.Builder builder) {
        builder.scalar(GraphQLScalarType.newScalar()
                .name("LocalDateTime")
                .description("LocalDateTime type")
                .coercing(new Coercing<LocalDateTime, String>() {

                    @Override
                    public String serialize(@Nonnull final Object dataFetcherResult,
                                            @Nonnull final GraphQLContext graphQLContext,
                                            @Nonnull final Locale locale) throws CoercingSerializeException {
                        if (dataFetcherResult instanceof LocalDateTime localDateTime) {
                            return FORMATTER.format(localDateTime);
                        } else {
                            throw new CoercingSerializeException("Expected a LocalDateTime object");
                        }
                    }

                    @Override
                    public LocalDateTime parseValue(@Nonnull final Object input,
                                                    @Nonnull final GraphQLContext graphQLContext,
                                                    @Nonnull final Locale locale) throws CoercingParseValueException {
                        try {
                            if (input instanceof String string) {
                                return LocalDateTime.parse(string);
                            } else {
                                throw new CoercingParseValueException("Expected a String value");
                            }
                        } catch (DateTimeParseException e) {
                            throw new CoercingParseValueException(input + " is not a valid date", e);
                        }
                    }

                    @Override
                    public LocalDateTime parseLiteral(@Nonnull final Value<?> input,
                                                      @Nonnull final CoercedVariables variables,
                                                      @Nonnull final GraphQLContext graphQLContext,
                                                      @Nonnull final Locale locale) throws CoercingParseLiteralException {
                        if (input instanceof StringValue stringValue) {
                            try {
                                final String value = stringValue.getValue();
                                if (value == null) {
                                    throw new CoercingParseLiteralException("Expected a non-null String literal");
                                }
                                return LocalDateTime.parse(value);
                            } catch (DateTimeParseException e) {
                                throw new CoercingParseLiteralException(e);
                            }
                        } else {
                            throw new CoercingParseLiteralException("Expected a String literal");
                        }
                    }
                }).build());
    }
}