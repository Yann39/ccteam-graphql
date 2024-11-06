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

import com.ccteam.graphql.config.graphql.CustomGraphQLException;
import com.ccteam.graphql.entities.Member;
import com.ccteam.graphql.entities.News;
import com.ccteam.graphql.repository.MemberRepository;
import com.ccteam.graphql.repository.NewsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * {@link News} service.
 *
 * @author yann39
 * @since 1.0.0
 */
@Service
@Slf4j
public class NewsService {

    private final NewsRepository newsRepository;
    private final MemberRepository memberRepository;

    public NewsService(NewsRepository newsRepository, MemberRepository memberRepository) {
        this.newsRepository = newsRepository;
        this.memberRepository = memberRepository;
    }

    /**
     * Get all news.
     *
     * @return A list of {@link News} objects representing the news
     */
    public List<News> getAllNews() {
        return newsRepository.findAllCustom();
    }

    /**
     * Get a specific news given its {@code id}.
     *
     * @param id The ID of the news to retrieve
     * @return A {@link News} object representing the news
     */
    public News getNewsById(Long id) {
        final Optional<News> newsOptional = newsRepository.findByIdCustom(id);
        if (newsOptional.isEmpty()) {
            log.error("News with id {} not found in the database", id);
            throw new CustomGraphQLException("news_not_found", "Specified news has not been found in the database");
        }
        return newsOptional.get();
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
    public List<News> getNewsFilteredPaginated(String text, int pageNumber, int pageSize, String sortBy, String sortDirection) {

        final News news = new News();
        news.setTitle(text);

        final ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withIgnorePaths("catchLine", "content", "newsDate", "createdOn", "createdBy", "modifiedOn", "modifiedBy")
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        final Page<News> newsPage = newsRepository.findFilteredCustom(Example.of(news, matcher), PageRequest.of(pageNumber, pageSize, sortDirection.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending()));

        if (newsPage.isEmpty()) {
            return new ArrayList<>();
        }
        return newsPage.toList();
    }

    /**
     * Create a new news.
     *
     * @param title     The news title
     * @param catchLine The news catch line
     * @param content   The news content
     * @param newsDate  The news date
     * @param memberId  The ID of the {@link Member} to set as creator of the news
     * @return A {@link News} object representing the news just created
     */
    public News createNews(String title, String catchLine, String content, String newsDate, long memberId) throws CustomGraphQLException {

        // get the current logged user
        /*final Object loggedUserPrincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(loggedUserPrincipal instanceof String loggedUserMail) || loggedUserMail.isEmpty()) {
            log.error("Cannot get user principal from security context");
            throw new CustomGraphQLException("user_not_logged", "Cannot get user principal from security context");
        }*/

        // check if specified member exists
        final Optional<Member> memberOptional = memberRepository.findByIdCustom(memberId);
        if (memberOptional.isEmpty()) {
            log.error("Member with ID {} not found in the database", memberId);
            throw new CustomGraphQLException("member_not_found", "Logged member has not been found in the database");
        }

        // create news with specified data
        final News news = new News();
        news.setTitle(title);
        news.setCatchLine(catchLine);
        news.setContent(content);
        news.setNewsDate(LocalDateTime.parse(newsDate));
        news.setCreatedBy(memberOptional.get());
        news.setCreatedOn(LocalDateTime.now());

        // save to database
        final News savedNews = newsRepository.save(news);

        // fetch again from database using our custom query so that lazy data is loaded
        return newsRepository.findByIdCustom(savedNews.getId()).orElseThrow();
    }

    /**
     * Update the news represented by the given ID with the specified data.
     *
     * @param newsId    The ID of the {@link News} to update
     * @param title     The news title
     * @param catchLine The news catch line
     * @param content   The news content
     * @param newsDate  The news date
     * @param memberId  The ID of the {@link Member} to set as last modifier of the news
     * @return A {@link News} object representing the news just updated
     */
    public News updateNews(long newsId, String title, String catchLine, String content, String newsDate, long memberId) {

        // check if specified news exists
        final Optional<News> newsOptional = newsRepository.findByIdCustom(newsId);
        if (newsOptional.isEmpty()) {
            log.error("News with id {} not found in the database", newsId);
            throw new CustomGraphQLException("news_not_found", "Specified news ID has not been found in the database");
        }

        // get the current logged user
        /*final Object loggedUserPrincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(loggedUserPrincipal instanceof String loggedUserMail) || loggedUserMail.isEmpty()) {
            log.error("Cannot get user principal from security context");
            throw new CustomGraphQLException("user_not_logged", "Cannot get user principal from security context");
        }*/

        // check if specified member exists
        final Optional<Member> memberOptional = memberRepository.findByIdCustom(memberId);
        if (memberOptional.isEmpty()) {
            log.error("Member with ID {} not found in the database", memberId);
            throw new CustomGraphQLException("member_not_found", "Logged member has not been found in the database");
        }

        // update news with specified data
        final News news = newsOptional.get();
        news.setTitle(title);
        news.setCatchLine(catchLine);
        news.setContent(content);
        news.setNewsDate(LocalDateTime.parse(newsDate));
        news.setModifiedBy(memberOptional.get());
        news.setModifiedOn(LocalDateTime.now());

        // save to database
        final News savedNews = newsRepository.save(news);

        // fetch again from database using our custom query so that lazy data is loaded
        return newsRepository.findByIdCustom(savedNews.getId()).orElseThrow();

    }

    /**
     * Delete the news represented by the given ID.
     *
     * @param newsId The ID of the {@link News} to delete
     * @return A {@link News} object representing the news just deleted
     */
    public News deleteNews(long newsId) throws CustomGraphQLException {

        // check if specified news exists
        final Optional<News> newsOptional = newsRepository.findByIdCustom(newsId);
        if (newsOptional.isEmpty()) {
            log.error("News with id {} not found in the database", newsId);
            throw new CustomGraphQLException("news_not_found", "Specified news ID has not been found in the database");
        }

        final News news = newsOptional.get();

        // delete from database
        newsRepository.delete(news);

        // return the original news
        return news;
    }

    /**
     * Mark the specified news as liked by the specified member.
     *
     * @param newsId   The news ID
     * @param memberId The member ID
     * @return A {@link News} object representing the news just liked
     */
    public News likeNews(long newsId, long memberId) throws CustomGraphQLException {

        // check that the news exists
        final Optional<News> newsOptional = newsRepository.findByIdCustom(newsId);
        if (newsOptional.isEmpty()) {
            log.error("News with id {} not found in the database", newsId);
            throw new CustomGraphQLException("news_not_found", "Specified news has not been found in the database");
        }

        // check that the member exists
        if (!memberRepository.existsById(memberId)) {
            log.error("Member with id {} not found in the database", memberId);
            throw new CustomGraphQLException("member_not_found", "Specified member has not been found in the database");
        }

        final News news = newsOptional.get();

        // check that the news is not already liked by the member
        if (news.getLikedNews().stream().anyMatch(ln -> ln.getNews().getId().equals(newsId) && ln.getMember().getId().equals(memberId))) {
            log.error("News with id {} already liked by member id {}", newsId, memberId);
            throw new CustomGraphQLException("news_already_liked_by_member", "Specified news is already liked by specified member");
        }

        // set the liked news for the member in the database
        final int nbUpdated = newsRepository.likeNews(memberId, newsId, LocalDateTime.now());
        if (nbUpdated < 1) {
            log.warn("Like news query returned 0 result");
        }

        // fetch the up-to-date news from the database
        final Optional<News> latestNewsOptional = newsRepository.findByIdCustom(newsId);
        if (latestNewsOptional.isEmpty()) {
            log.error("News with id {} not found in the database after processing", newsId);
            throw new CustomGraphQLException("news_not_found", "News has not been found in the database after processing");
        }

        return latestNewsOptional.get();
    }

    /**
     * Mark the specified news as not liked by the specified member.
     *
     * @param newsId   The news ID
     * @param memberId The member ID
     * @return A {@link News} object representing the news just unliked
     */
    public News unlikeNews(long newsId, long memberId) throws CustomGraphQLException {

        // check that news exists
        final Optional<News> newsOptional = newsRepository.findByIdCustom(newsId);
        if (newsOptional.isEmpty()) {
            log.error("News with id {} not found in the database", newsId);
            throw new CustomGraphQLException("news_not_found", "Specified news has not been found in the database");
        }

        // check that member exists
        if (!memberRepository.existsById(memberId)) {
            log.error("Member with id {} not found in the database", memberId);
            throw new CustomGraphQLException("member_not_found", "Specified member has not been found in the database");
        }

        final News news = newsOptional.get();

        // check that the news is indeed liked by the member
        if (news.getLikedNews().stream().noneMatch(ln -> ln.getNews().getId().equals(newsId) && ln.getMember().getId().equals(memberId))) {
            log.error("News with id {} is not liked by member id {}, cannot unlike", newsId, memberId);
            throw new CustomGraphQLException("news_not_liked_by_member", "Specified news is not liked by specified member");
        }

        // remove the liked news for the member in the database
        final int nbUpdated = newsRepository.unlikeNews(memberId, newsId);
        if (nbUpdated < 1) {
            log.warn("Unlike news query returned 0 result");
        }

        // fetch the up-to-date news from the database
        final Optional<News> latestNewsOptional = newsRepository.findByIdCustom(newsId);
        if (latestNewsOptional.isEmpty()) {
            log.error("News with id {} not found in the database after processing", newsId);
            throw new CustomGraphQLException("news_not_found", "News has not been found in the database after processing");
        }

        return latestNewsOptional.get();
    }

}