package net.consensys.web3auth.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.module.application.model.Application.JwtSetting;

@Service
@Slf4j
public class JwtService {

    
    public JwtService() { }
    
    public String generateToken(JwtSetting jwtSetting, String owner) {
        
        log.debug("generateToken(owner={})", owner);
        
        Date expirationDate = Date.from(LocalDateTime.now().plusSeconds(jwtSetting.getExpiration()).toInstant(ZoneOffset.UTC));

        String token = Jwts.builder()
               .setSubject(owner)
               .setExpiration(expirationDate)
               .setIssuer(jwtSetting.getIssuer())
               .setIssuedAt(new Date())
               .signWith(SignatureAlgorithm.HS512, jwtSetting.getSecret())
               .compact();
        
        log.debug("generateToken(owner={}): {}", owner, token);
        
        return token;
    }

    /**
     * Validate a token
     * @param token
     * @return
     */
   public Boolean validateToken(JwtSetting jwtSetting, String  token) {
        
        try {
            Jwts.parser().setSigningKey(jwtSetting.getSecret()).parseClaimsJws(token);
            return true;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (ExpiredJwtException e) {
            log.warn("the token is expired and not valid anymore", e);
            throw e;
        }
    }
   
    /**
     * Read a token and  a return the subject/username (address)
     * @param token
     * @return subject/username (address)
     */
    public String getUsernameFromToken(JwtSetting jwtSetting, String token) {
        return getClaimFromToken(jwtSetting, token, Claims::getSubject);
    }

    /**
     * Read a token and  a return the issued date of the token
     * @param token
     * @return issued date
     */
    public Date getIssuedAtDateFromToken(JwtSetting jwtSetting, String token) {
        return getClaimFromToken(jwtSetting, token, Claims::getIssuedAt);
    }

    /**
     * Read a token and a return the expiration date of the token
     * @param token
     * @return expiration date
     */
    public Date getExpirationDateFromToken(JwtSetting jwtSetting, String token) {
        return getClaimFromToken(jwtSetting, token, Claims::getExpiration);
    }  
    
    /**
     * Read the token and return a specific claim
     * @param token
     * @param claimsResolver
     * @return claim
     */
    public <T> T getClaimFromToken(JwtSetting jwtSetting, String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser().setSigningKey(jwtSetting.getSecret()).parseClaimsJws(token).getBody();

        return claimsResolver.apply(claims);
    } 
    
}
