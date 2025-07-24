package ma.enset.ebankingbackend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class SecurityController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtEncoder jwtEncoder;

    @GetMapping("/profile")
    public Map<String, Object> authentication(Authentication auth) {
        return Map.of(
                "username", auth.getName(),
                "roles", auth.getAuthorities()
        );
    }

    @PostMapping("/login")
    public Map<String, String> login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        Instant now = Instant.now();
        String scope = authentication.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.joining(" "));
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(now.plus(10, ChronoUnit.MINUTES))
                .subject(username)
                .claim("scope", scope)
                .build();
        JwtEncoderParameters jwtEncoderParameters =
                JwtEncoderParameters.from(
                        JwsHeader.with(MacAlgorithm.HS512).build(),
                        jwtClaimsSet
                );
        String jwt = jwtEncoder.encode(jwtEncoderParameters).getTokenValue();
        return Map.of("access_token", jwt);
    }

}
