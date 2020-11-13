package com.chachatte.graphql.graphql.query;

import com.chachatte.graphql.dto.MemberDto;
import com.chachatte.graphql.entities.Member;
import com.chachatte.graphql.repository.MemberRepository;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.annotation.Secured;
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
public class MemberQueryResolver implements GraphQLQueryResolver {

    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    public MemberQueryResolver(MemberRepository memberRepository, ModelMapper modelMapper) {
        this.memberRepository = memberRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Get all members.
     *
     * @return A list of DTO objects representing the members
     */
    @Secured("ROLE_MEMBER")
    public Iterable<MemberDto> getAllMembers() {
        log.info("Received call to getAllMembers");
        final List<Member> result = new ArrayList<>();
        memberRepository.findAll().forEach(result::add);
        return result.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    /**
     * Get all members that have liked the news identified by the specified {@code newsId}.
     *
     * @param newsId The news ID
     * @return A list of DTO objects representing the members
     */
    @Secured("ROLE_MEMBER")
    public Iterable<MemberDto> getMembersLikedNews(long newsId) {
        log.info("Received call to getAllMembers");
        return memberRepository.findByNewsId(newsId).stream().map(this::convertToDto).collect(Collectors.toList());
    }

    /**
     * Get all members according to the specified filter {@code text}.
     * Search is done on first name, last name and e-mail address.
     *
     * @param text The text filter
     * @return A list of DTO objects representing the members
     */
    @Secured("ROLE_MEMBER")
    public Iterable<MemberDto> getMembersFiltered(String text) {
        log.info("Received call to getMembersFiltered with parameter text = " + text);
        if (text != null && text.length() > 0) {
            return memberRepository.findByFirstNameLikeOrLastNameLikeOrEmailLike("%" + text + "%", "%" + text + "%", "%" + text + "%").stream().map(this::convertToDto).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /**
     * Convert the specified {@link Member} object to an {@link MemberDto} object.
     *
     * @param member The member to convert
     * @return The {@link MemberDto} object created
     */
    private MemberDto convertToDto(Member member) {
        return modelMapper.map(member, MemberDto.class);
    }
}