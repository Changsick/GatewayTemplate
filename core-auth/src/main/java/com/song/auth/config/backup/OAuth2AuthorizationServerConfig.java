package com.song.auth.config.backup;

import org.springframework.context.annotation.Configuration;

@Configuration
public class OAuth2AuthorizationServerConfig {


    //    @Bean
//    @Order(1)
//    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
//        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
//                OAuth2AuthorizationServerConfigurer.authorizationServer();
//
//        http
//                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
//                .with(authorizationServerConfigurer, (authorizationServer) ->
//                        authorizationServer
//                                .oidc(Customizer.withDefaults())	// Enable OpenID Connect 1.0
//                )
//                .authorizeHttpRequests((authorize) ->
//                        authorize
//                                .anyRequest().authenticated()
//                )
//                // Redirect to the login page when not authenticated from the
//                // authorization endpoint
//                .exceptionHandling((exceptions) -> exceptions
//                        .defaultAuthenticationEntryPointFor(
//                                new LoginUrlAuthenticationEntryPoint("/login"),
//                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
//                        )
//                );
//
//        return http.build();
//    }
//
//    @Bean
//    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
//        return new JdbcRegisteredClientRepository(jdbcTemplate);
//    }
//
//
//    @Bean
//    public AuthorizationServerSettings authorizationServerSettings() {
//        return AuthorizationServerSettings.builder()
//                .issuer("http://localhost:9091")
//                .build();
//    }

//    @Bean
//    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
//        OAuth2AuthorizationServerConfigurer<HttpSecurity> authorizationServerConfigurer =
//                new OAuth2AuthorizationServerConfigurer<>();
//
//        // OAuth2 관련 설정
//        authorizationServerConfigurer
//                .tokenEndpoint(token -> token
//                        .accessTokenRequestConverter(new CustomTokenRequestConverter()) // 커스텀 토큰 요청 변환기 (필요 시)
//                )
//                .clientAuthentication(client -> client
//                        .clientIdParameter("client_id")
//                        .clientSecretParameter("client_secret")
//                );
//
//        // OAuth2 인증 관련 보안 설정을 적용합니다.
//        authorizationServerConfigurer.configure(http);
//
//        return http.build();
//    }
}
