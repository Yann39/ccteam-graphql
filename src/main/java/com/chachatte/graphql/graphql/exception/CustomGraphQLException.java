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

package com.chachatte.graphql.graphql.exception;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.List;
import java.util.Map;

/**
 * Custom GraphQL exception to customize the returned error.<br/>
 * <p>
 * It implements {@link GraphQLError} so that GraphQL can return an error message
 * in a format that the client-side is expecting.
 *
 * @author ybailly
 * @since sept 2020
 */
public class CustomGraphQLException extends RuntimeException implements GraphQLError {

    private static final long serialVersionUID = 1240866421447805573L;

    public CustomGraphQLException(String message) {
        super(message);
    }

    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }

    @Override
    public ErrorType getErrorType() {
        return null;
    }

    @Override
    public Map<String, Object> getExtensions() {
        return null;
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        return null;
    }
}