package org.example.digibankjwt.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.example.digibankjwt.config.JwtProperties;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    private final JwtProperties props;
    private final SecretKey secretKey;

    public JwtService(JwtProperties props){
        this.props = props;
        this.secretKey = Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDetails userDetails, long expirationMillis, boolean isRefresh) {
        String role = userDetails.getAuthorities().stream().map(auth-> auth.getAuthority()).findFirst().orElse("ROLE_USER");

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("role", role)
                .claim("type", isRefresh ? "refresh" : "access") //  add token type
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractTokenType(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("type", String.class);
    }


    public String extractUserRole(String token){
        return Jwts.parserBuilder() // build the jwt parser
                .setSigningKey(secretKey) // with our secretKey
                .build()
                .parseClaimsJws(token)// parse the token
                .getBody()// gives all the payload
                .get("role",String.class); // extract the role
    }


    public String extractUsername(String token){
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }



    public boolean isTokenExpired(String token){
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails){
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

}
