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

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.stereotype.Component;

/**
 * Custom GraphQL exception handler, for all post-filter requests handled by GraphQL.
 * <p>
 * It catches all the exceptions and transfers them to the appropriate {@link GraphQLError}
 * implementation to be passed to the client-side for handling.
 *
 * @author ybailly
 * @since 1.0.0
 */
@Component
public class CustomDataFetchingExceptionHandler extends DataFetcherExceptionResolverAdapter {

    @Override
    public GraphQLError resolveToSingleError(Throwable throwable, DataFetchingEnvironment dataFetchingEnvironment) {
        if (throwable instanceof CustomGraphQLException customGraphQLException) {
            return GraphqlErrorBuilder.newError(dataFetchingEnvironment)
                    .message(throwable.getMessage())
                    .extensions((customGraphQLException).getExtensions())
                    .errorType((customGraphQLException).getErrorType())
                    .build();
        }
        return null;
    }

}