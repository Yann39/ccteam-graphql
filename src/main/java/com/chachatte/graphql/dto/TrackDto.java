package com.chachatte.graphql.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author yann39
 * @since sept 2020
 */
@Getter
@Setter
public class TrackDto {

    private Long id;
    private String name;
    private int distance;
    private int lapRecord;
    private String website;
    private BigDecimal latitude;
    private BigDecimal longitude;

}
