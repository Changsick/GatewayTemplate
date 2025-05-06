package com.song.gateway.config;

import com.song.gateway.support.CustomJwtAuthenticationManager;
import com.song.gateway.support.RedisService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {
    private final RedisService redisService;
    private final ReactiveJwtDecoder jwtDecoder;

    public GatewaySecurityConfig(RedisService redisService, ReactiveJwtDecoder jwtDecoder) {
        this.redisService = redisService;
        this.jwtDecoder = jwtDecoder;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(ex -> ex
//                        .pathMatchers("/server1/**").authenticated()
                        .anyExchange().authenticated()
                )
//                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
                        .jwtDecoder(jwtDecoder)
                        .authenticationManager(new CustomJwtAuthenticationManager(jwtDecoder, redisService))
                ))
                .build();
    }
}
