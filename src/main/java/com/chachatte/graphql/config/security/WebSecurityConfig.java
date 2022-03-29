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

package com.chachatte.graphql.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

/**
 * Web security configuration.
 * <p>
 * It enables Spring HTTP Security, by providing a default configuration.
 *
 * @author yann39
 * @since 1.0.0
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    @Autowired
    private JWTTokenUtils jwtTokenUtils;

    /**
     * Define a new role hierarchy.
     *
     * @return A {@link RoleHierarchyImpl} object representing the new role hierarchy implementation
     */
    @Bean
    public RoleHierarchyImpl roleHierarchy() {
        final RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_MEMBER > ROLE_USER");
        return roleHierarchy;
    }

    /**
     * Define a new security expression handler with the new defined role hierarchy, to be used instead
     * of the default {@link DefaultWebSecurityExpressionHandler} so we can use our custom role hierarchy.
     *
     * @return A {@link RoleHierarchyImpl} object representing the new role hierarchy implementation
     */
    @Bean
    public SecurityExpressionHandler<FilterInvocation> webExpressionHandler() {
        final DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy());
        return expressionHandler;
    }

    /**
     * Needed to prevent the {@link JWTAuthorizationFilter} filter from being called twice.<br/>
     * Indeed, Spring Boot automatically registers any bean that is a Filter with the servlet container.<br/>
     * In our case we still need the filter to be a bean/component to be able to autowire dependencies in it,
     * this is why we use this registration bean, to tell Spring Boot not to register the filter again.
     * <p>
     *
     * @param filter The filter to disable
     * @return A {@link FilterRegistrationBean} bean with the passed filter disabled
     */
    @Bean
    public FilterRegistrationBean<JWTAuthorizationFilter> disableJWTAuthorizationFilter(final JWTAuthorizationFilter filter) {
        final FilterRegistrationBean<JWTAuthorizationFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(filter);
        filterRegistrationBean.setEnabled(false);
        return filterRegistrationBean;
    }

    /**
     * HTTP security configuration for authenticated content.
     *
     * @param http The {@link HttpSecurity}
     * @return A {@link SecurityFilterChain} object representing the new configured filter chain
     * @throws Exception An exception occurred while configuring the HTTP security
     */
    @Bean
    @Order(1)
    public SecurityFilterChain filterChainAuthenticated(HttpSecurity http) throws Exception {
        http
                // disable CSRF as we do not serve browser clients
                .csrf().disable()
                // match GraphQL endpoint
                .antMatcher("/graphql")
                // add JWT authorization filter
                .addFilter(new JWTAuthorizationFilter(new CustomDbAuthenticationManager(), jwtTokenUtils))
                // allow access restriction using request matcher
                .authorizeRequests()
                // apply our custom web expression handler because we added role hierarchy to it
                .expressionHandler(webExpressionHandler())
                // authenticate requests to GraphQL endpoint
                .antMatchers("/graphql").authenticated()
                // allow any other requests
                .anyRequest().permitAll().and()
                // custom exception handling
                .exceptionHandling().authenticationEntryPoint(new JWTAuthenticationEntryPoint()).and()
                // make sure we use stateless session, session will not be used to store user's state
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }

    /**
     * HTTP security configuration for non-authenticated content.
     * <p>
     * We use that second configuration because we need to apply the custom authentication filter only
     * for non-authenticated content.
     *
     * @param http The {@link HttpSecurity}
     * @return A {@link SecurityFilterChain} object representing the new configured filter chain
     * @throws Exception An exception occurred while configuring the HTTP security
     */
    @Bean
    @Order(2)
    public SecurityFilterChain filterChainNotAuthenticated(HttpSecurity http) throws Exception {
        http
                // disable CSRF as we do not serve browser clients
                .csrf().disable()
                // custom exception handling
                .exceptionHandling().authenticationEntryPoint(new CustomDbAuthenticationEntryPoint()).and()
                // make sure we use stateless session, session will not be used to store user's state
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }

}