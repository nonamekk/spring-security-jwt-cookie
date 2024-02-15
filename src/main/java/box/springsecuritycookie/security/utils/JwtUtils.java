package box.springsecuritycookie.security.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {
    private @Value("${jwt.token-key}") String SECRET_KEY;

    public <T> T extractClaim(String token, @NotNull Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Boolean validateToken(String token, @NotNull UserDetails userDetails) {
        final String username = extractSubject(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public ResponseCookie generateCookie(UserDetails userDetails) {
        String token = generateToken(userDetails);
        return createCookie(token);
    }

    private String generateToken(@NotNull UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities",userDetails.getAuthorities());
        return createToken(claims, userDetails.getUsername());
    }

    private ResponseCookie createCookie(String token) {
        return ResponseCookie
                .from("__Host-auth-token", token)
                .path("/")
                .domain(null)
                .secure(true)
                .httpOnly(true)
                .maxAge(7 * 24 * 60 * 60)
                .build();
    }

    public ResponseCookie getCleanJwtCookie() {
        ResponseCookie cookie = ResponseCookie
                .from("__Host-auth-token", null)
                .path("/")
                .domain(null)
                .secure(true)
                .httpOnly(true)
                .maxAge(0)
                .build();
        return cookie;
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    private @NotNull Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
