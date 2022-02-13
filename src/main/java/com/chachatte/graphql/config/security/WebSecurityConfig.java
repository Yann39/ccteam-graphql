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

package com.chachatte.graphql.config.security;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

/**
 * Enable Spring HTTP Security, by extending {@link WebSecurityConfigurerAdapter} to provide a
 * default configuration.
 *
 * @author yann39
 * @since oct 2020
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JWTTokenUtils jwtTokenUtils;

    @Bean
    protected AuthenticationManager getAuthenticationManager() throws Exception {
        return authenticationManager();
    }

    @Bean
    public RoleHierarchyImpl roleHierarchy() {
        final RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_MEMBER > ROLE_USER");
        return roleHierarchy;
    }

    @Bean
    public SecurityExpressionHandler<FilterInvocation> webExpressionHandler() {
        DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy());
        return expressionHandler;
    }

    @Bean
    protected ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Needed to prevent the {@link JWTAuthorizationFilter} filter from being called twice.<br/>
     * Indeed, Spring Boot automatically registers any bean that is a Filter with the servlet container.<br/>
     * We still need the filter to be a bean/component to be able to autowire dependencies in it,
     * this is why we use this registration bean, to tell Spring Boot not to register the filter again.
     * <p>
     * //@param filter The filter to disable
     *
     * @return A {@link FilterRegistrationBean} bean with the passed filter disabled
     */
    @Bean
    public FilterRegistrationBean disableJWTAuthorizationFilter(final JWTAuthorizationFilter filter) {
        final FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(filter);
        filterRegistrationBean.setEnabled(false);
        return filterRegistrationBean;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // disable CSRF as we do not serve browser clients
                .csrf().disable()
                // allow access restriction using request matcher
                .authorizeRequests()
                // apply our custom web expression handler because we added role hierarchy to it
                .expressionHandler(webExpressionHandler())
                // authenticate requests to GraphQL endpoint
                .antMatchers("/graphql").authenticated()
                // allow all other requests
                .anyRequest().permitAll().and()
                // JWT authorization filter
                .addFilter(new JWTAuthorizationFilter(getAuthenticationManager(), jwtTokenUtils))
                // custom exception handling
                .exceptionHandling().authenticationEntryPoint(new JWTAuthenticationEntryPoint()).and()
                // make sure we use stateless session, session will not be used to store user's state
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

}