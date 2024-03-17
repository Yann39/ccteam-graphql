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

import graphql.language.IntValue;
import graphql.schema.*;
import graphql.schema.idl.RuntimeWiring;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeParseException;

/**
 * Custom GraphQL scalar implementation of the {@link Long} type.
 * <p>
 * It can parse a GraphQL {@link IntValue} object to a Java {@link Long} object,
 * and serialize a {@link Long} object as {@link Integer}.
 *
 * @author ybailly
 * @since 1.0.0
 */
@Component
public class LongScalar implements RuntimeWiringConfigurer {

    @Override
    public void configure(RuntimeWiring.Builder builder) {
        builder.scalar(GraphQLScalarType.newScalar()
                .name("Long")
                .description("Long type")
                .coercing(new Coercing<Long, Integer>() {

                    @Override
                    public Integer serialize(final Object dataFetcherResult) {
                        if (dataFetcherResult instanceof Long value) {
                            return Math.toIntExact(value);
                        } else {
                            throw new CoercingSerializeException("Expected a Long object");
                        }
                    }

                    @Override
                    public Long parseValue(final Object input) {
                        try {
                            if (input instanceof Integer value) {
                                return Long.valueOf(value);
                            } else {
                                throw new CoercingParseValueException("Expected an Integer value");
                            }
                        } catch (DateTimeParseException e) {
                            throw new CoercingParseValueException(input + " is not a valid integer", e);
                        }
                    }

                    @Override
                    public Long parseLiteral(final Object input) {
                        if (input instanceof IntValue value) {
                            try {
                                return (value).getValue().longValue();
                            } catch (DateTimeParseException e) {
                                throw new CoercingParseLiteralException(e);
                            }
                        } else {
                            throw new CoercingParseLiteralException("Expected an Int literal");
                        }
                    }
                }).build());
    }

}

