package com.song.auth.config.backup;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RegisteredClientInitializer {

    /*
    private final RegisteredClientRepository registeredClientRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisteredClientInitializer(@Qualifier("registeredClientRepository") RegisteredClientRepository registeredClientRepository, PasswordEncoder passwordEncoder) {
        this.registeredClientRepository = registeredClientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        if (registeredClientRepository.findByClientId("my-client-id") == null) {
            RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
//                    .clientId("my-client-id")
//                    .clientSecret(passwordEncoder.encode("secret")) // 또는 {bcrypt}
//                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
//                    .authorizationGrantType(AuthorizationGrantType.PASSWORD) // 또는 AUTHORIZATION_CODE 등
//                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
//                    .scope("read")
//                    .scope("write")
//                    .tokenSettings(TokenSettings.builder()
//                            .accessTokenTimeToLive(Duration.ofMinutes(30))
//                            .refreshTokenTimeToLive(Duration.ofDays(1))
//                            .build())
//                    .clientSettings(ClientSettings.builder()
//                            .requireAuthorizationConsent(false)
//                            .build())
                    .clientId("my-client-id")
                    .clientSecret(passwordEncoder.encode("my-client-secret"))
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                    .redirectUri("http://localhost:9091/login")
                    .scope(OidcScopes.OPENID)
                    .scope("profile")
                    .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                    .tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofHours(1)).build())
                    .build();

            registeredClientRepository.save(registeredClient);
        }
    }
    */

}
