package com.chachatte.graphql.resolver.query;

import com.chachatte.graphql.dto.MemberDto;
import com.chachatte.graphql.dto.NewsDto;
import com.chachatte.graphql.entities.Member;
import com.chachatte.graphql.entities.News;
import com.chachatte.graphql.repository.MemberRepository;
import com.chachatte.graphql.repository.NewsRepository;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
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
    public Iterable<NewsDto> getAllNews() {
        log.info("Received call to getAllNews");
        final List<News> result = new ArrayList<>();
        newsRepository.findAll().forEach(result::add);
        return result.stream().map(this::convertToNewsDto).collect(Collectors.toList());
    }

    /**
     * Get all news with the specified {@code title}.
     *
     * @param title The news title
     * @return A list of DTO objects representing the news
     */
    public Iterable<NewsDto> getNewsByTitle(String title) {
        log.info("Received call to getEventsByTitle with parameter title = " + title);
        return newsRepository.findByTitle(title).stream().map(this::convertToNewsDto).collect(Collectors.toList());
    }

    /**
     * Convert the specified {@link News} object to an {@link NewsDto} object.
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
     * Convert the specified {@link Member} object to an {@link MemberDto} object.
     *
     * @param member The member to convert
     * @return The {@link MemberDto} object created
     */
    private MemberDto convertToMemberDto(Member member) {
        return modelMapper.map(member, MemberDto.class);
    }
}