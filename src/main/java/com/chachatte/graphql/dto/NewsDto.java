package com.chachatte.graphql.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author yann39
 * @since sept 2020
 */
@Getter
@Setter
public class NewsDto {

    private Long id;
    private String title;
    private String catchLine;
    private String content;
    private LocalDateTime newsDate;
    private List<MemberDto> likedMembers;
    private LocalDateTime createdOn = LocalDateTime.now();
    private MemberDto createdBy;
    private LocalDateTime modifiedOn;
    private MemberDto modifiedBy;

}
