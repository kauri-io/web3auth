package net.consensys.web3auth.common;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.configuration.Web3AuthSettings.JwtSetting;
import net.consensys.web3auth.exception.TokenException;

@Slf4j
public class JwtUtils {

    private JwtUtils() {}

    public static String generateToken(JwtSetting jwtSetting, String owner) {
        return generateToken(jwtSetting, owner, null);
    }
    
    public static String generateToken(JwtSetting jwtSetting, String owner, Map<String, Object> claims) {
        
        log.debug("generateToken(owner={}, claims={})", owner, claims);
        
        Date expirationDate = Date.from(LocalDateTime.now().plusSeconds(jwtSetting.getExpiration()).toInstant(ZoneOffset.UTC));

        JwtBuilder builder = Jwts.builder()
               .setSubject(owner)
               .setExpiration(expirationDate)
               .setIssuer(jwtSetting.getIssuer())
               .setIssuedAt(new Date());
        
        if(claims != null) {
            claims.forEach((key,value) -> builder.claim(key, value));
        }
               
        String token = builder.signWith(SignatureAlgorithm.HS512, jwtSetting.getSecret()).compact();
        
        log.debug("generateToken(owner={}, claims={}): {}", owner, claims, token);
        
        return token;
    }

    /**
     * Validate a token
     * @param token
     * @return
     */
   public static Boolean validateToken(JwtSetting jwtSetting, String token) {
        
        try {
            Jwts.parser().setSigningKey(jwtSetting.getSecret()).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException|MalformedJwtException|SignatureException e) {
            throw new TokenException(token, e.getMessage());
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
     * Read a token and a return the a custom element
     * @param token
     * @param key
     * @return Object for the given key
     */
    public static Object getCustomClaimFromToken(JwtSetting jwtSetting, String token, String key) {
        return getClaimFromToken(jwtSetting, token, claim -> claim.get(key));
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
