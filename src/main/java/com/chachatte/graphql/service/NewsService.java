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

package com.chachatte.graphql.service;

import com.chachatte.graphql.entities.News;
import com.chachatte.graphql.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * Mail utilities.
 *
 * @author yann39
 * @since nov 2020
 */
@Service
public class NewsService {

    @Autowired
    private NewsRepository newsRepository;

    public Iterable<News> getNewsFilteredPaginated(String text, int pageNumber, int pageSize, String sortBy, String sortDirection) {

        final News news = new News();
        news.setTitle(text);

        final ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withIgnorePaths("catchLine", "content", "newsDate", "createdOn", "createdBy", "modifiedOn", "modifiedBy")
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        return newsRepository.findAll(Example.of(news, matcher), PageRequest.of(pageNumber, pageSize, sortDirection.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending()));

    }
}