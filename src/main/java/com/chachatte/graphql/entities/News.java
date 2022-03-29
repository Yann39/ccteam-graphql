/*
 * Copyright (c) 2022 by Yann39
 *
 * This file is part of Chachatte Team GraphQL application.
 *
 * Chachatte Team GraphQL is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Chachatte Team GraphQL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with Chachatte Team GraphQL. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.chachatte.graphql.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author yann39
 * @since 1.0.0
 */
@Getter
@Setter
@Entity
@Table(name = "news")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 128, nullable = false)
    private String title;

    @Column(columnDefinition = "longtext", length = 512, nullable = false)
    private String catchLine;

    @Column(columnDefinition = "longtext", nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime newsDate;

    @OneToMany(mappedBy = "news", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LikedNews> likedNews = new HashSet<>();

    @Column(nullable = false)
    private LocalDateTime createdOn = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Member createdBy;

    @Column
    private LocalDateTime modifiedOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modified_by")
    private Member modifiedBy;

    /**
     * Set the current news to be liked by the specified {@code member}.
     *
     * @param member The {@link Member} to be defined as having liked the news
     */
    public void addLikedNews(Member member) {
        final LikedNews lNews = new LikedNews();
        lNews.setNews(this);
        lNews.setMember(member);
        lNews.setCreatedOn(LocalDateTime.now());
        likedNews.add(lNews);
        member.getLikedNews().add(lNews);
    }

    /**
     * Set the current news to be not liked by the specified {@code member}.
     *
     * @param member The {@link Member} to be removed from having liked the news
     */
    public void removeLikedNews(Member member) {
        for (Iterator<LikedNews> iterator = likedNews.iterator();
             iterator.hasNext(); ) {
            final LikedNews lNews = iterator.next();
            if (lNews.getNews().equals(this) &&
                    lNews.getMember().equals(member)) {
                iterator.remove();
                lNews.getMember().getLikedNews().remove(lNews);
                lNews.setNews(null);
                lNews.setMember(null);
            }
        }
    }

}
