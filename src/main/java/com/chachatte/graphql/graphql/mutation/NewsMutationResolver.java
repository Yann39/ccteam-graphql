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

package com.chachatte.graphql.graphql.mutation;

import com.chachatte.graphql.dto.LikedNewsDto;
import com.chachatte.graphql.dto.MemberDto;
import com.chachatte.graphql.dto.NewsDto;
import com.chachatte.graphql.entities.LikedNews;
import com.chachatte.graphql.entities.Member;
import com.chachatte.graphql.entities.News;
import com.chachatte.graphql.repository.LikedNewsRepository;
import com.chachatte.graphql.repository.MemberRepository;
import com.chachatte.graphql.repository.NewsRepository;
import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author yann39
 * @since sept 2020
 */
@Component
@Slf4j
public class NewsMutationResolver implements GraphQLMutationResolver {

    private final NewsRepository newsRepository;
    private final MemberRepository memberRepository;
    private final LikedNewsRepository likedNewsRepository;
    private final ModelMapper modelMapper;

    public NewsMutationResolver(NewsRepository newsRepository, MemberRepository memberRepository, LikedNewsRepository likedNewsRepository, ModelMapper modelMapper) {
        this.newsRepository = newsRepository;
        this.memberRepository = memberRepository;
        this.likedNewsRepository = likedNewsRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Create a new news.
     *
     * @param title     The news title
     * @param catchLine The news catch line
     * @param content   The news content
     * @param newsDate  The news date
     * @return A DTO object representing the news just created
     */
    @Secured("ROLE_ADMIN")
    public NewsDto newNews(String title, String catchLine, String content, String newsDate) {
        log.info("Received call to newNews with parameters title = " + title + ", catchLine = " + catchLine + ", content = " + content + ", newsDate = " + newsDate);
        final News news = new News();
        news.setTitle(title);
        news.setCatchLine(catchLine);
        news.setContent(content);
        news.setNewsDate(LocalDateTime.parse(newsDate));
        final News created = newsRepository.save(news);
        return convertToNewsDto(created);
    }

    /**
     * Mark the specified news as liked by the specified member.
     *
     * @param newsId   The news ID
     * @param memberId The member ID
     * @return A DTO object representing the news just liked
     */
    @Secured("ROLE_MEMBER")
    public NewsDto likeNews(long newsId, long memberId) {
        log.info("Received call to likeNews with parameters newsId = " + newsId + ", memberId = " + memberId);

        final Optional<News> news = newsRepository.findById(newsId);
        if (news.isEmpty()) {
            log.error("News with id " + newsId + " not found in the database");
            return null;
        }

        final Optional<Member> member = memberRepository.findById(memberId);
        if (member.isEmpty()) {
            log.error("Member with id " + memberId + " not found in the database");
            return null;
        }

        if (likedNewsRepository.existsByNewsIdAndMemberId(news.get().getId(), member.get().getId())) {
            log.error("News with id " + newsId + " already liked by member id " + memberId);
            return null;
        }

        final LikedNews likedNews = new LikedNews();
        likedNews.setNews(news.get());
        likedNews.setMember(member.get());
        likedNews.setCreatedOn(LocalDateTime.now());

        final LikedNews created = likedNewsRepository.save(likedNews);

        return convertToNewsDto(created.getNews());
    }

    /**
     * Mark the specified news as not liked by the specified member.
     *
     * @param newsId   The news ID
     * @param memberId The member ID
     * @return {@code true} if the unlike succeeded, {@code false} if not
     */
    @Secured("ROLE_MEMBER")
    public NewsDto unlikeNews(long newsId, long memberId) {
        log.info("Received call to unlikeNews with parameters newsId = " + newsId + ", memberId = " + memberId);

        final Optional<News> news = newsRepository.findById(newsId);
        if (news.isEmpty()) {
            log.error("News with id " + newsId + " not found in the database");
            return null;
        }
        final Optional<Member> member = memberRepository.findById(memberId);
        if (member.isEmpty()) {
            log.error("Member with id " + memberId + " not found in the database");
            return null;
        }

        if (!likedNewsRepository.existsByNewsIdAndMemberId(news.get().getId(), member.get().getId())) {
            log.error("News with id " + newsId + " is not liked by member id " + memberId + ", cannot unlike");
            return null;
        }

        final LikedNews likedNews = likedNewsRepository.findByNewsIdAndMemberId(news.get().getId(), member.get().getId());
        likedNewsRepository.delete(likedNews);

        return convertToNewsDto(likedNews.getNews());
    }

    /**
     * Convert the specified {@link News} object to a {@link NewsDto} object
     *
     * @param news The news to convert
     * @return The {@link NewsDto} object created
     */
    private NewsDto convertToNewsDto(News news) {
        final NewsDto newsDto = modelMapper.map(news, NewsDto.class);
        final List<MemberDto> memberDtos = memberRepository.findByNewsId(news.getId()).stream().map(this::convertToMemberDto).collect(Collectors.toList());
        newsDto.setLikedMembers(memberDtos);
        return newsDto;
    }

    /**
     * Convert the specified {@link Member} entity object to a {@link MemberDto} DTO object.
     *
     * @param member The member to convert
     * @return The {@link MemberDto} object created
     */
    private MemberDto convertToMemberDto(Member member) {
        return modelMapper.map(member, MemberDto.class);
    }

    /**
     * Convert the specified {@link LikedNews} object to a {@link LikedNewsDto} object
     *
     * @param likedNews The likedNews to convert
     * @return The {@link LikedNewsDto} object created
     */
    private LikedNewsDto convertToLikedNewsDto(LikedNews likedNews) {
        return modelMapper.map(likedNews, LikedNewsDto.class);
    }
}