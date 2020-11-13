package com.chachatte.graphql.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author yann39
 * @since sept 2020
 */
@Getter
@Setter
@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 64, nullable = false)
    private String firstName;

    @Column(length = 64, nullable = false)
    private String lastName;

    @Column(length = 128, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 13)
    private String phone;

    @Column
    private String avatarUrl;

    @Column(length = 128)
    private String bike;

    @Column(nullable = false)
    private boolean active = false;

    @Column(nullable = false)
    private boolean admin = false;

    @Enumerated(EnumType.STRING)
    @Column
    private Role role;

    @Column(nullable = false)
    private LocalDateTime registrationDate;

    @Column(nullable = false)
    private LocalDateTime createdOn = LocalDateTime.now();

    @Column
    private LocalDateTime modifiedOn;

    public enum Role implements GrantedAuthority {
        ROLE_USER,
        ROLE_MEMBER,
        ROLE_ADMIN;

        @Override
        public String getAuthority() {
            return this.name();
        }
    }

}
