package org.fathy.fawrylms.security;

import org.fathy.fawrylms.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final String audience;
    private final long expirationMs;

    public JwtService(
            JwtEncoder jwtEncoder,
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.audience}") String audience,
            @Value("${jwt.expiration-ms}") long expirationMs
    ) {
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.audience = audience;
        this.expirationMs = expirationMs;
    }

    public String generateToken(User user) {
        Instant issuedAt = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .audience(List.of(audience))
                .subject(user.getId().toString())
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plusMillis(expirationMs))
                .claim("email", user.getEmail())
                .claim("roles", user.getAuthorities().stream()
                        .map(authority -> authority.getAuthority())
                        .toList())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256)
                .type("JWT")
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
