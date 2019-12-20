package net.consensys.web3auth.common;

import static java.lang.Math.toIntExact;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.consensys.web3auth.configuration.Web3AuthSettings.CookieSetting;


public class CookieUtils {

    private CookieUtils() { }
    
    public static void addCookie(CookieSetting cookieSetting, HttpServletResponse response, String name, String value, Date expiration, boolean httpOnly) {
        
        LocalDateTime expirationDateLocalDate = Instant.ofEpochMilli(expiration.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        int maxAge = toIntExact(LocalDateTime.now().until(expirationDateLocalDate, ChronoUnit.SECONDS));
        
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath(cookieSetting.getPath());
        cookie.setDomain(cookieSetting.getDomain());
        cookie.setSecure(cookieSetting.isSecure());
        cookie.setHttpOnly(httpOnly);

        response.addCookie(cookie);
    }
    
    public static void deleteCookie(CookieSetting cookieSetting, HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setMaxAge(0);
        cookie.setPath(cookieSetting.getPath());
        cookie.setDomain(cookieSetting.getDomain());
        cookie.setSecure(cookieSetting.isSecure());

        response.addCookie(cookie);
    }

    public static Optional<String> getCookieValue(HttpServletRequest request, String cookieName) {
        if(request.getCookies() == null) {
            return Optional.empty();
        }
        
        return Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals(cookieName))
                .findFirst()
                .map(Cookie::getValue);
    }
    


    
}
