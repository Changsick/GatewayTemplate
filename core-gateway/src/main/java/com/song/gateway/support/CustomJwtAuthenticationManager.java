package com.song.gateway.support;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

public class CustomJwtAuthenticationManager implements ReactiveAuthenticationManager {
    private final ReactiveJwtDecoder jwtDecoder;
    private final RedisService redisService;

    public CustomJwtAuthenticationManager(ReactiveJwtDecoder jwtDecoder, RedisService redisService) {
        this.jwtDecoder = jwtDecoder;
        this.redisService = redisService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();
        System.out.println("token: " + token);

        // 블랙리스트에 있는지 확인
        return redisService.isBlacklisted(token)
                .flatMap(isBlacklisted -> {
                    if (isBlacklisted) {
                        return Mono.error(new BadCredentialsException("Token is blacklisted"));
                    }

                    // JWT 디코딩 및 검증
                    return jwtDecoder.decode(token)
                            .map(jwt -> new JwtAuthenticationToken(jwt, AuthorityUtils.NO_AUTHORITIES));
                });
    }
}
