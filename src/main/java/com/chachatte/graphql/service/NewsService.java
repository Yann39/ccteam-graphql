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

package com.chachatte.graphql.service;

import com.chachatte.graphql.config.graphql.CustomGraphQLException;
import com.chachatte.graphql.entities.Member;
import com.chachatte.graphql.entities.News;
import com.chachatte.graphql.projection.NewsDetailsProjection;
import com.chachatte.graphql.projection.NewsListProjection;
import com.chachatte.graphql.repository.MemberRepository;
import com.chachatte.graphql.repository.NewsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private MemberRepository memberRepository;

    /**
     * Get all news.
     *
     * @return A list of {@link News} objects representing the news
     */
    public List<News> getAllNews() {
        return newsRepository.findAllCustom();
    }

    /**
     * Get all news, with only minimum properties to be used in home list.
     *
     * @return A list of {@link NewsListProjection} objects representing the news list
     */
    public List<NewsListProjection> getAllNewsForHomeList() {
        return newsRepository.findAllCustomForHomeList();
    }

    /**
     * Get a news given its {@code id}.
     *
     * @param id The ID of the news to retrieve
     * @return A {@link News} object representing the news
     */
    public News getNewsById(Long id) throws CustomGraphQLException {
        final Optional<News> newsOptional = newsRepository.findByIdCustom(id);
        if (newsOptional.isEmpty()) {
            log.error("News with id " + id + " not found in the database");
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
     * Get a news given its {@code id}, with only minimum properties to be used in news detail view.
     *
     * @param id The ID of the news to retrieve
     * @return A {@link NewsDetailsProjection} object representing the news
     */
    public NewsDetailsProjection getNewsByIdForDetailsView(Long id) throws CustomGraphQLException {
        final Optional<NewsDetailsProjection> newsOptional = newsRepository.findOneCustomForDetailsView(id);
        if (newsOptional.isEmpty()) {
            log.error("News with id " + id + " not found in the database");
            throw new CustomGraphQLException("news_not_found", "Specified news has not been found in the database");
        }
        return newsOptional.get();
    }

    /**
     * Create new news.
     *
     * @param title     The news title
     * @param catchLine The news catch line
     * @param content   The news content
     * @param newsDate  The news date
     * @param memberId  The ID of the {@link Member} creating the news
     * @return A {@link News} object representing the news just created
     */
    public News createNews(String title, String catchLine, String content, String newsDate, long memberId) throws CustomGraphQLException {

        // check if specified member exists
        final Optional<Member> memberOptional = memberRepository.findByIdCustom(memberId);
        if (memberOptional.isEmpty()) {
            log.error("Member with id " + memberId + " not found in the database");
            throw new CustomGraphQLException("member_not_found", "Specified member ID has not been found in the database");
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
     * Update the news represented by the given news ID with the specified data.
     *
     * @param newsId    The ID of the {@link News} to update
     * @param title     The news title
     * @param catchLine The news catch line
     * @param content   The news content
     * @param newsDate  The news date
     * @param memberId  The ID of the {@link Member} creating the news
     * @return A {@link News} object representing the news just updated
     */
    public News updateNews(long newsId, String title, String catchLine, String content, String newsDate, long memberId) throws CustomGraphQLException {

        // check if specified news exists
        final Optional<News> newsOptional = newsRepository.findByIdCustom(newsId);
        if (newsOptional.isEmpty()) {
            log.error("News with id " + newsId + " not found in the database");
            throw new CustomGraphQLException("news_not_found", "Specified news ID has not been found in the database");
        }

        // check if specified member exists
        final Optional<Member> memberOptional = memberRepository.findByIdCustom(memberId);
        if (memberOptional.isEmpty()) {
            log.error("Member with id " + memberId + " not found in the database");
            throw new CustomGraphQLException("member_not_found", "Specified member ID has not been found in the database");
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
     * Delete the news represented by the given news ID.
     *
     * @param newsId The ID of the {@link News} to delete
     * @return A {@link News} object representing the news just deleted
     */
    public News deleteNews(long newsId) throws CustomGraphQLException {

        // check if specified news exists
        final Optional<News> newsOptional = newsRepository.findByIdCustom(newsId);
        if (newsOptional.isEmpty()) {
            log.error("News with id " + newsId + " not found in the database");
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
     * @return A DTO object representing the news just liked
     */
    public News likeNews(long newsId, long memberId) throws CustomGraphQLException {

        // check that news exists
        final Optional<News> newsOptional = newsRepository.findByIdCustom(newsId);
        if (newsOptional.isEmpty()) {
            log.error("News with id " + newsId + " not found in the database");
            throw new CustomGraphQLException("news_not_found", "Specified news has not been found in the database");
        }

        // check that member exists
        final Optional<Member> memberOptional = memberRepository.findByIdCustom(memberId);
        if (memberOptional.isEmpty()) {
            log.error("Member with id " + memberId + " not found in the database");
            throw new CustomGraphQLException("member_not_found", "Specified member has not been found in the database");
        }

        final News news = newsOptional.get();
        final Member member = memberOptional.get();

        // check that the news is not already liked by the member
        if (news.getLikedNews().stream().anyMatch(ln -> ln.getNews().getId().equals(newsId) && ln.getMember().getId().equals(memberId))) {
            log.error("News with id " + newsId + " already liked by member id " + memberId);
            throw new CustomGraphQLException("news_already_liked_by_member", "Specified news is already liked by specified member");
        }

        // set news to be liked by member
        news.addLikedNews(member);

        // save to database
        return newsRepository.save(news);
    }

    /**
     * Mark the specified news as not liked by the specified member.
     *
     * @param newsId   The news ID
     * @param memberId The member ID
     * @return {@code true} if the unlike succeeded, {@code false} if not
     */
    public News unlikeNews(long newsId, long memberId) throws CustomGraphQLException {
        // check that news exists
        final Optional<News> newsOptional = newsRepository.findByIdCustom(newsId);
        if (newsOptional.isEmpty()) {
            log.error("News with id " + newsId + " not found in the database");
            throw new CustomGraphQLException("news_not_found", "Specified news has not been found in the database");
        }

        // check that member exists
        final Optional<Member> memberOptional = memberRepository.findByIdCustom(memberId);
        if (memberOptional.isEmpty()) {
            log.error("Member with id " + memberId + " not found in the database");
            throw new CustomGraphQLException("member_not_found", "Specified member has not been found in the database");
        }

        final News news = newsOptional.get();
        final Member member = memberOptional.get();

        // check that the news is indeed liked by the member
        if (news.getLikedNews().stream().noneMatch(ln -> ln.getNews().getId().equals(newsId) && ln.getMember().getId().equals(memberId))) {
            log.error("News with id " + newsId + " is not liked by member id " + memberId + ", cannot unlike");
            throw new CustomGraphQLException("news_not_liked_by_member", "Specified news is not liked by specified member");
        }

        // remove the liked news for the member
        news.removeLikedNews(member);

        // save to database
        return newsRepository.save(news);

    }

}