package com.luysot.jobodia.util;

import com.luysot.jobodia.model.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    @Value("${jwt.secret.key}")
    private String jwtSecretKey;

    private final long EXPIRATION = 10000 * 60 * 60 * 24; //24 Hours

    public SecretKey getSigningKeys(){
        return Keys.hmacShaKeyFor(
                jwtSecretKey.getBytes(StandardCharsets.UTF_8)
        );
    }

    public Claims getClaim(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKeys())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(Users user){
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() +EXPIRATION))
                .signWith(getSigningKeys(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmailFromToken(String token){
        return getClaim(token).getSubject();
    }

    public boolean validateToken(String email, UserDetails userDetail, String token){
        try{
            getClaim(token);
            return true;
        }
        catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }
}
