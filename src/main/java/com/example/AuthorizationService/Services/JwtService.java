package com.example.AuthorizationService.Services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service

public class JwtService  {
    @Value("${jwt.expiry}")
    private int expiry;

    @Value("${jwt.secret}")
    private String SECRET;

    /**
     * This method create a new jwt token based on a payload
     * @return
     */
    public String createToken(Map<String ,Object>payload,String email){
        Date now= new Date();
        Date expiryDate= new Date(now.getTime() + expiry*1000L);
        return Jwts.builder()
                .claims(payload)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(expiryDate)
                .subject(email)
                .signWith(getSignKey())
                .compact();
    }

    public String createToken(String email){
        return createToken(new HashMap<>(),email);
    }

    public SecretKey getSignKey(){
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }
    public Claims extractAllPayLoads(String token){
        return Jwts.parser()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims=extractAllPayLoads(token);
        return claimsResolver.apply(claims);
    }
    public Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }
    public String extractEmail(String token){
        return extractClaim(token,Claims::getSubject);
    }

    /*
     * this method checks if token expiry was before the current time stamp or not
     * token Jwt token
     * return true if is expired else false
     * */
    public Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    public Boolean validateToken(String token,String email){
        final String userEmailFetchedFromToken=extractEmail(token);
        return (userEmailFetchedFromToken.equals(email) && !isTokenExpired(token));
    }
    public String extractPayload(String token,String payloadKey){
        Claims claims=extractAllPayLoads(token);
        return (String)claims.get("phone");

    }

}
