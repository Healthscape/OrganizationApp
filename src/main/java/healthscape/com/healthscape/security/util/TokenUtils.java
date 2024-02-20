package healthscape.com.healthscape.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;


@Component
@Slf4j
public class TokenUtils {

    @Value("${jwt.secret}")
    public String SECRET;
    @Value("${spring.application.name}")
    private String APP_NAME;
    @Value("${jwt.expiration}")
    private int EXPIRES_IN;

    @Value("${jwt.refresh-expiration}")
    private int REFRESH_EXPIRES_IN;


    @Value("Authorization")
    private String AUTH_HEADER;


    public String generateToken(String username, String role, List<String> authorities) {
        log.debug("GT - user: {}", username);
        Date exp_date = new Date(new Date().getTime() + EXPIRES_IN);
        return Jwts.builder().issuer(APP_NAME).subject(username).claim("roles", role).claim("authorities", authorities).expiration(exp_date).issuedAt(new Date()).signWith(getSigningKey()).compact();

    }

    public String generateRefreshToken(String username, String role, List<String> authorities) {
        log.debug("GT - user: {}", username);
        Date exp_date = new Date(new Date().getTime() + REFRESH_EXPIRES_IN);
        return Jwts.builder().issuer(APP_NAME).subject(username).claim("roles", role).claim("authorities", authorities).expiration(exp_date).issuedAt(new Date()).signWith(getSigningKey()).compact();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = SECRET.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Date generateExpirationDate(Boolean isRefreshToken) {
        Date date;
        if (isRefreshToken.equals(true)) {
            date = new Date(new Date().getTime() + 3600000);
        } else {
            date = new Date(new Date().getTime() + EXPIRES_IN);
        }
        return date;
    }


    public String getToken(HttpServletRequest request) {
        String authHeader = getAuthHeaderFromHeader(request);


        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }

    public String getEmailFromToken(String token) {
        log.debug("GET ET");
        String username;

        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            username = claims.getSubject();
        } catch (ExpiredJwtException ex) {
            log.debug("GET ET - TE");
            throw ex;
        } catch (Exception e) {
            log.warn("GET ET FAILED");
            username = null;
        }

        return username;
    }

    public String getRoleFromToken(String token) {
        log.debug("GET RT");
        String role;

        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            role = claims.get("roles", String.class);
        } catch (ExpiredJwtException ex) {
            log.debug("GET RT - TE");
            throw ex;
        } catch (Exception e) {
            log.warn("GET RT FAILED");
            role = null;
        }

        return role;
    }

    public Date getIssuedAtDateFromToken(String token) {
        Date issueAt;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            issueAt = claims.getIssuedAt();
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            issueAt = null;
        }
        return issueAt;
    }

    public Date getExpirationDateFromToken(String token) {
        log.debug("GET ED");
        Date expiration;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (ExpiredJwtException ex) {
            log.debug("GET ED - TE");
            throw ex;
        } catch (Exception e) {
            log.warn("GET ED FAILED");
            expiration = null;
        }

        return expiration;
    }

    private Claims getAllClaimsFromToken(String token) {
        log.debug("GET AC");
        Claims claims;
        try {
            claims = Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException ex) {
            log.debug("GET AC - TE");
            throw ex;
        } catch (Exception e) {
            log.warn("GET AC FAILED");
            claims = null;
        }


        return claims;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getEmailFromToken(token);
        log.debug("VT - user: {}", username);
        return (username != null && username.equals(userDetails.getUsername()));
    }

    public Boolean isTokenExpired(String token) {
        Date date = getExpirationDateFromToken(token);
        return date.before(new Date());
    }

    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }

    public int getExpiredIn() {
        return EXPIRES_IN;
    }

    public String getAuthHeaderFromHeader(HttpServletRequest request) {
        return request.getHeader(AUTH_HEADER);
    }

}