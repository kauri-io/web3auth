package net.consensys.web3auth.module.adapter.springsecurity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.common.Constant;
import net.consensys.web3auth.common.dto.AccountDetails;
import net.consensys.web3auth.common.dto.ClientDetails;
import net.consensys.web3auth.common.dto.ClientType;
import net.consensys.web3auth.common.dto.exception.HTTPClientException;
import net.consensys.web3auth.common.service.Web3AuthWSClient;

@Slf4j
public class AuthorisationFilter extends OncePerRequestFilter {
    
    private final ClientDetails client;
    private final String authorizationHeader;
    private final Web3AuthWSClient web3AuthWSClient;
    
    public AuthorisationFilter(ClientDetails client, String authorizationHeader, Web3AuthWSClient web3AuthWSClient) {
        this.client = client;
        this.authorizationHeader = authorizationHeader;
        this.web3AuthWSClient = web3AuthWSClient;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            final String remoteAddr =  getRemoteAddress(request);
            Optional<String> token = getToken(request, client, authorizationHeader);
            
            if(token.isPresent()) {
                AccountDetails details = parseAndValidateToken(token.get(), web3AuthWSClient);
                
                Web3AuthAuthenticationToken identifiedAuth = new Web3AuthAuthenticationToken(details, token.get(), remoteAddr);
                identifiedAuth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(identifiedAuth);
                log.debug("User authenticated [remoteAddress: {}, ID: {}]", identifiedAuth.getRemoteAddress(), identifiedAuth.getName());
            }

            // Next
            filterChain.doFilter(request, response);
            
        } catch (HTTPClientException ex) {
            log.warn("Error while executing the filter 'AuthorisationFilter':" + ex.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());

        } catch(Exception ex) {
            log.warn("Error while executing the filter 'AuthorisationFilter':" + ex.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }
    
    
    private static String decode(String value) throws UnsupportedEncodingException {
        return Optional.ofNullable(value).map(v -> {
            try {
                return URLDecoder.decode(v, StandardCharsets.UTF_8.toString());
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        }).orElse(null);
    }
    
    public static Optional<String> getCookieValue(HttpServletRequest req, String cookieName) {
        
        if(req.getCookies() == null) {
            return Optional.empty();
        }
        
        return Arrays.stream(req.getCookies())
                .filter(c -> c.getName().equals(cookieName))
                .findFirst()
                .map(Cookie::getValue);
    }
    
    public static Optional<String> getToken(HttpServletRequest req, ClientDetails client, String authorizationHeader) throws UnsupportedEncodingException {
        
        Optional<String> token = Optional.empty();

        if(client.getType().equals(ClientType.BEARER) || client.getType().equals(ClientType.BOTH)) {
            String authenticationHeader = req.getHeader(authorizationHeader);
            String authenticationParameter = decode(req.getParameter(authorizationHeader));
    
            if (authenticationHeader != null && authenticationHeader.startsWith("Bearer ")) {
                token = Optional.of(authenticationHeader.substring(7));
                
            } else if (authenticationParameter != null && authenticationParameter.startsWith("Bearer ")) {
                token = Optional.of(authenticationParameter.substring(7));
            }
            
            if(token.isPresent()) {
                return token;
            }
        }
        
        if(client.getType().equals(ClientType.BROWSER) || client.getType().equals(ClientType.BOTH)) {
            token = getCookieValue(req, Constant.COOKIE_TOKEN);
        } 
        
        return token;
    }
    
    public static AccountDetails parseAndValidateToken(String token, Web3AuthWSClient client) throws IOException {
        AccountDetails tokenDetails = client.getAccountByToken(token);
        log.trace("AccountDetails = {}", tokenDetails);
        return tokenDetails;
    }
    
    public static String getRemoteAddress(HttpServletRequest req) {
        String remoteAddr = "";

        if (req != null) {
            remoteAddr = req.getHeader("x-forwarded-for");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = req.getRemoteAddr();
            }
        }
        return remoteAddr;
    }

}
