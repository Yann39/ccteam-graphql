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

import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Web security configuration.
 * <p>
 * It enables Spring HTTP Security, by providing a default configuration.
 *
 * @author yann39
 * @since 1.0.0
 */
@Configuration
@EnableMethodSecurity
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
     * Define a new method security expression handler with the new defined role hierarchy, to be used instead
     * of the default one.
     *
     * @return A {@link DefaultMethodSecurityExpressionHandler} object representing the new configured expression handler
     */
    @Bean
    public DefaultMethodSecurityExpressionHandler expressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy());
        return expressionHandler;
    }

    /**
     * Define a password encoder that uses the BCrypt strong hashing function.
     *
     * @return A {@link PasswordEncoder} object representing the {@link BCryptPasswordEncoder} implementation
     */
    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Needed to prevent the {@link JWTAuthorizationFilter} filter from being called twice.<br/>
     * Indeed, Spring Boot automatically registers any bean that is a Filter with the servlet container.<br/>
     * In our case we still need the filter to be a bean/component to be able to auto-wire dependencies in it,
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
     * Define the authentication manager for the current authentication request, according to the current authentication configuration.
     *
     * @param authenticationConfiguration The {@link AuthenticationConfiguration} that exports the current authentication configuration.
     * @return The {@link AuthenticationManager} for the current authentication request
     * @throws Exception An exception occurred while getting authentication manager from authentication configuration
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * HTTP security configuration.
     *
     * @param http The {@link HttpSecurity}
     * @return A {@link SecurityFilterChain} object representing the new configured filter chain
     * @throws Exception An exception occurred while configuring the HTTP security
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                // disable CSRF as we do not serve browser clients
                .csrf().disable()
                // add JWT authorization filter
                .addFilter(new JWTAuthorizationFilter(authenticationManager(http.getSharedObject(AuthenticationConfiguration.class)), jwtTokenUtils))
                // allow restricting access to certain URL based on the HTTP servlet request
                .authorizeHttpRequests()
                // by default, the AuthorizationFilter applies to all dispatcher types, so grant all access only on requests with dispatcher type ASYNC or FORWARD
                .dispatcherTypeMatchers(DispatcherType.ASYNC, DispatcherType.FORWARD).permitAll()
                // authenticate requests to GraphQL endpoint
                .requestMatchers("/graphql").authenticated()
                // allow any request to REST endpoint
                .requestMatchers("/rest/**").permitAll()
                // deny any other requests
                .anyRequest().denyAll().and()
                // custom exception handling
                .exceptionHandling().authenticationEntryPoint(new JWTAuthenticationEntryPoint()).and()
                // make sure we use stateless session, session will not be used to store user's state
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .build();

    }

}