package com.chachatte.graphql.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author yann39
 * @since sept 2020
 */
@Getter
@Setter
public class EventDto {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private TrackDto track;
    private String organizer;
    private BigDecimal price;
    private LocalDateTime createdOn = LocalDateTime.now();
    private MemberDto createdBy;
    private LocalDateTime modifiedOn;
    private MemberDto modifiedBy;

}
