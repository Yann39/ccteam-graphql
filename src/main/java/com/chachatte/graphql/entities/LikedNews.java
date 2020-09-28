package com.chachatte.graphql.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author yann39
 * @since sept 2020
 */
@Getter
@Setter
@Entity
@Table(name = "liked_news")
public class LikedNews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    Member member;

    @ManyToOne
    @JoinColumn(name = "news_id")
    News news;

    @Column(nullable = false)
    private LocalDateTime createdOn = LocalDateTime.now();

}
