package com.ecommerce.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoder;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${spring.app.jwtSecretKey}")
    private String jwtSecretKey;

    @Value("${spring.app.jwtExprationTimeMs}")
    private Long jwtExpirationTimeMs;

    private static final Logger logger= LoggerFactory.getLogger(JwtUtils.class);

    //Extract token from header
    public String getJWTFromHeader(HttpServletRequest request){
        String bearerToken=request.getHeader("Authorization");
        if (bearerToken!=null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }

    //Generate token from username
    public String getJWTFromUsername(UserDetails userDetails){
        String username = userDetails.getUsername();

        Date expiry=Date.from(
                Instant.now().plusMillis(jwtExpirationTimeMs)
        );

        return Jwts.builder()
                .setSubject(username)
                .issuedAt(new Date())
                .expiration(expiry)
                .compact();
    }

    //Get username for Token
    public String getUsernameFromJWT(String jwtToken){
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload()
                .getSubject();
    }

    //Generate key
    public Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretKey));
    }

    //Validate JWT
    public boolean validateJWT(String jwtToken){
        try{
            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parseSignedClaims(jwtToken)
                    .getPayload();
            return true;
        }catch (MalformedJwtException e){
            logger.error("Invalid JWT token :{}",e.getMessage());
        }catch (ExpiredJwtException e){
            logger.error("Expired JWT token: {}",e.getMessage());
        }catch (IllegalArgumentException e){
            logger.error("JWT claims are empty: {}",e.getMessage());
        }catch (UnsupportedJwtException e){
            logger.error("Unsupported JWT token: {}",e.getMessage());
        }
        return false;
    }

}
