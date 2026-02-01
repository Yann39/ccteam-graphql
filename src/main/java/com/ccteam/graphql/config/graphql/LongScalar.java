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
                .coercing(new Coercing<Long, Object>() {

                    @Override
                    public Object serialize(final Object dataFetcherResult) {
                        return dataFetcherResult instanceof Long value ? value : dataFetcherResult.toString();
                    }

                    @Override
                    public Long parseValue(final Object input) {
                        if (input instanceof Number number) {
                            return number.longValue();
                        } else if (input instanceof String string) {
                            try {
                                return Long.parseLong(string);
                            } catch (NumberFormatException e) {
                                throw new CoercingParseValueException("Value is not a valid Long: " + input);
                            }
                        }
                        throw new CoercingParseValueException("Expected a Number or String value, but got: "
                                + (input == null ? "null" : input.getClass().getName()));
                    }

                    @Override
                    public Long parseLiteral(final Object input) {
                        if (input instanceof IntValue value) {
                            return value.getValue().longValue();
                        } else if (input instanceof graphql.language.StringValue value) {
                            try {
                                return Long.parseLong(value.getValue());
                            } catch (NumberFormatException e) {
                                throw new CoercingParseLiteralException(
                                        "Value is not a valid Long: " + value.getValue());
                            }
                        } else {
                            throw new CoercingParseLiteralException("Expected an Int or String literal");
                        }
                    }
                }).build());
    }

}
