package com.song.auth.api;

import com.song.auth.api.dto.AuthRequest;
import com.song.auth.api.dto.AuthResponse;
import com.song.auth.webService.RedisService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtEncoder jwtEncoder;
    private final RedisService redisService;

    public AuthController(AuthenticationManager authManager, JwtEncoder jwtEncoder, RedisService redisService) {
        this.authManager = authManager;
        this.jwtEncoder = jwtEncoder;
        this.redisService = redisService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        Instant now = Instant.now();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(
                JwtClaimsSet.builder()
                        .issuer("http://localhost:9091")
                        .issuedAt(now)
                        .expiresAt(now.plus(1, ChronoUnit.HOURS))
                        .subject(request.username())
                        .claim("scope", "USER")
                        .build()
        )).getTokenValue();

        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(name = "Authorization") String accessToken) {
        redisService.blacklistToken(accessToken, 3600);
        return ResponseEntity.ok().build();
    }


}
