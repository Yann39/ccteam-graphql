/*
 * Copyright (c) 2022 by Yann39
 *
 * This file is part of Chachatte Team GraphQL application.
 *
 * Chachatte Team GraphQL is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Chachatte Team GraphQL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with Chachatte Team GraphQL. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.chachatte.graphql.controller.graphql;

import com.chachatte.graphql.entities.Member;
import com.chachatte.graphql.entities.News;
import com.chachatte.graphql.service.NewsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * {@link News} GraphQL controller.
 *
 * @author yann39
 * @since 1.0.0
 */
@Controller
@Slf4j
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    /**
     * Get all news.
     *
     * @return A list of {@link News} objects representing the news
     */
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @QueryMapping
    public List<News> getAllNews() {
        log.info("Received call to getAllNews");
        return newsService.getAllNews();
    }

    /**
     * Get all news given the search {@code text}, and according to specified pagination information.
     *
     * @param text          A {@link String} to search in the news title or description
     * @param pageNumber    The page number for the pagination
     * @param pageSize      The page size for the pagination
     * @param sortBy        The column name for the sorting
     * @param sortDirection The sort direction ({@code asc} or {@code desc})
     * @return A list of {@link News} object representing the news
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @QueryMapping
    public List<News> getAllNewsFilteredPaginated(@Argument String text,
                                                  @Argument int pageNumber,
                                                  @Argument int pageSize,
                                                  @Argument String sortBy,
                                                  @Argument String sortDirection) {
        log.info("Received call to getAllNewsFilteredPaginated with parameter text = {}, pageNumber = {}, pageSize = {}, sortBy = {}, sortDirection = {}",
                text, pageNumber, pageSize, sortBy, sortDirection);
        return newsService.getNewsFilteredPaginated(text, pageNumber, pageSize, sortBy, sortDirection);
    }

    /**
     * Get a news given its {@code id}.
     *
     * @param id The ID of the news to retrieve
     * @return A {@link News} object representing the news
     */
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @QueryMapping
    public News getNewsById(@Argument Long id) {
        log.info("Received call to getNewsById with parameter ID = {}", id);
        return newsService.getNewsById(id);
    }

    /**
     * Create new news.
     *
     * @param title     The news title
     * @param catchLine The news catch line
     * @param content   The news content
     * @param newsDate  The news date
     * @param memberId  The ID of the {@link Member} to set as creator of the news
     * @return A {@link News} object representing the news just created
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @MutationMapping
    public News createNews(@Argument String title,
                           @Argument String catchLine,
                           @Argument String content,
                           @Argument String newsDate,
                           @Argument long memberId) {
        log.info("Received call to createNews with parameters title = {}, catchLine = {}, content = {}, newsDate = {}, memberId = {}",
                title, catchLine, content, newsDate, memberId);
        return newsService.createNews(title, catchLine, content, newsDate, memberId);
    }

    /**
     * Update the news represented by the given news ID with the specified data.
     *
     * @param newsId    The ID of the {@link News} to update
     * @param title     The news title
     * @param catchLine The news catch line
     * @param content   The news content
     * @param newsDate  The news date
     * @param memberId  The ID of the {@link Member} to set as last modifier of the news
     * @return A {@link News} object representing the news just created
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @MutationMapping
    public News updateNews(@Argument long newsId,
                           @Argument String title,
                           @Argument String catchLine,
                           @Argument String content,
                           @Argument String newsDate,
                           @Argument long memberId) {
        log.info("Received call to updateNews with parameters newsId = {}, title = {}, catchLine = {}, content = {}, newsDate = {}, memberId = {}",
                newsId, title, catchLine, content, newsDate, memberId);
        return newsService.updateNews(newsId, title, catchLine, content, newsDate, memberId);
    }

    /**
     * Delete the news represented by the given news ID.
     *
     * @param newsId The ID of the {@link News} to delete
     * @return A {@link News} object representing the news just deleted
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @MutationMapping
    public News deleteNews(@Argument long newsId) {
        log.info("Received call to deleteNews with parameter newsId = {}", newsId);
        return newsService.deleteNews(newsId);
    }

    /**
     * Mark the specified news as liked by the specified member.
     *
     * @param newsId   The news ID
     * @param memberId The member ID
     * @return A DTO object representing the news just liked
     */
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @MutationMapping
    public News likeNews(@Argument long newsId,
                         @Argument long memberId) {
        log.info("Received call to likeNews with parameters newsId = {}, memberId = {}", newsId, memberId);
        return newsService.likeNews(newsId, memberId);
    }

    /**
     * Mark the specified news as not liked by the specified member.
     *
     * @param newsId   The news ID
     * @param memberId The member ID
     * @return {@code true} if the unlike succeeded, {@code false} if not
     */
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @MutationMapping
    public News unlikeNews(@Argument long newsId,
                           @Argument long memberId) {
        log.info("Received call to unlikeNews with parameters newsId = {}, memberId = {}", newsId, memberId);
        return newsService.unlikeNews(newsId, memberId);
    }

}