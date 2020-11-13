package com.chachatte.graphql;

import com.chachatte.graphql.config.security.JWTTokenProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JWTTokenProperties.class)
public class ChachatteTeamGraphQLApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChachatteTeamGraphQLApplication.class, args);
    }

}
