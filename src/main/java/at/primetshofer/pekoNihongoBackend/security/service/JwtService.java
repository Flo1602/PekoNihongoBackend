package at.primetshofer.pekoNihongoBackend.security.service;

import at.primetshofer.pekoNihongoBackend.security.exception.TokenAuthenticationException;
import at.primetshofer.pekoNihongoBackend.security.user.AuthUser;
import at.primetshofer.pekoNihongoBackend.security.user.Role;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private static final String ROLES_CLAIM = "roles";

    private final Algorithm signingAlgorithm;

    public JwtService(@Value("${jwt.signing-secret}") String signingSecret) {

        this.signingAlgorithm = Algorithm.HMAC256(signingSecret);
    }

    public AuthUser resolveJwtToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(signingAlgorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);

            String userId = decodedJWT.getSubject();
            List<Role> roles = decodedJWT.getClaim(ROLES_CLAIM).asList(Role.class);

            return new AuthUser(Long.valueOf(userId), roles);
        } catch (JWTVerificationException exception) {
            throw new TokenAuthenticationException("JWT is not valid");
        }
    }

    public String createJwtToken(AuthUser authUser) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        long expMillis = nowMillis + 3600000; // 1 hour validity

        if(authUser.roles().contains(Role.USER)) {
            long oneYearMillis = 365L * 24 * 60 * 60 * 1000;
            expMillis = nowMillis + oneYearMillis; //1 year validity
        }

        Date exp = new Date(expMillis);

        List<String> roles = authUser.roles().stream().map(Role::name).toList();

        return JWT.create()
                .withSubject(authUser.userId() + "")
                .withClaim(ROLES_CLAIM, roles)
                .withIssuedAt(now)
                .withExpiresAt(exp)
                .sign(signingAlgorithm);
    }
}
