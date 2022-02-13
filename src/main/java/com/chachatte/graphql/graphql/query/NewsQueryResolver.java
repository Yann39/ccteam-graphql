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

package com.chachatte.graphql.graphql.query;

import com.chachatte.graphql.dto.MemberDto;
import com.chachatte.graphql.dto.NewsDto;
import com.chachatte.graphql.entities.Member;
import com.chachatte.graphql.entities.News;
import com.chachatte.graphql.graphql.exception.CustomGraphQLException;
import com.chachatte.graphql.repository.MemberRepository;
import com.chachatte.graphql.repository.NewsRepository;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author yann39
 * @since sept 2020
 */
@Component
@Slf4j
public class NewsQueryResolver implements GraphQLQueryResolver {

    private final NewsRepository newsRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    public NewsQueryResolver(NewsRepository newsRepository, MemberRepository memberRepository, ModelMapper modelMapper) {
        this.newsRepository = newsRepository;
        this.memberRepository = memberRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Get all news.
     *
     * @return A list of DTO objects representing the news
     */
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public Iterable<NewsDto> getAllNews() throws CustomGraphQLException {
        log.info("Received call to getAllNews");
        final List<News> result = new ArrayList<>(newsRepository.findAll());
        return result.stream().map(this::convertToNewsDto).collect(Collectors.toList());
    }

    /**
     * Get a news given its {@code id}.
     *
     * @param id The news ID
     * @return A list of DTO objects representing the news
     */
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public NewsDto getNewsById(Long id) {
        log.info("Received call to getNewsById with parameter id = " + id);
        final Optional<News> news = newsRepository.findById(id);
        return news.map(this::convertToNewsDto).orElse(null);
    }

    /**
     * Convert the specified {@link News} entity object to a {@link NewsDto} DTO object.
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
}