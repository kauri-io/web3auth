package net.consensys.web3auth.module.common;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.function.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.module.application.model.Application.JwtSetting;
import net.consensys.web3auth.module.common.exception.ExpiredTokenException;
import net.consensys.web3auth.module.common.exception.InvalidTokenException;

@Slf4j
public class JwtUtils {

    private JwtUtils() {}
    
    public static String generateToken(JwtSetting jwtSetting, String owner) {
        
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
   public static Boolean validateToken(JwtSetting jwtSetting, String  token) {
        
        try {
            Jwts.parser().setSigningKey(jwtSetting.getSecret()).parseClaimsJws(token);
            return true;
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException(e);
        } catch (ExpiredJwtException e) {
            log.warn("the token is expired and not valid anymore", e);
            throw new ExpiredTokenException(e);
        }
    }
   
    /**
     * Read a token and  a return the subject/username (address)
     * @param token
     * @return subject/username (address)
     */
    public static String getUsernameFromToken(JwtSetting jwtSetting, String token) {
        return getClaimFromToken(jwtSetting, token, Claims::getSubject);
    }

    /**
     * Read a token and  a return the issued date of the token
     * @param token
     * @return issued date
     */
    public static Date getIssuedAtDateFromToken(JwtSetting jwtSetting, String token) {
        return getClaimFromToken(jwtSetting, token, Claims::getIssuedAt);
    }

    /**
     * Read a token and a return the expiration date of the token
     * @param token
     * @return expiration date
     */
    public static Date getExpirationDateFromToken(JwtSetting jwtSetting, String token) {
        return getClaimFromToken(jwtSetting, token, Claims::getExpiration);
    }  
    
    /**
     * Read the token and return a specific claim
     * @param token
     * @param claimsResolver
     * @return claim
     */
    public static <T> T getClaimFromToken(JwtSetting jwtSetting, String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser().setSigningKey(jwtSetting.getSecret()).parseClaimsJws(token).getBody();

        return claimsResolver.apply(claims);
    } 
    
}
