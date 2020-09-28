package com.chachatte.graphql.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author yann39
 * @since sept 2020
 */
@Getter
@Setter
public class MemberDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private String avatarUrl;
    private String bike;
    private boolean active = false;
    private boolean admin = false;
    private LocalDateTime registrationDate;
    private LocalDateTime createdOn;
    private LocalDateTime modifiedOn;

}
