package net.consensys.web3auth.module.adapter.springsecurity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.common.Constant;
import net.consensys.web3auth.common.dto.ClientDetails;
import net.consensys.web3auth.common.dto.ClientType;
import net.consensys.web3auth.common.dto.Organisation;
import net.consensys.web3auth.common.dto.TokenDetails;
import net.consensys.web3auth.common.service.CookieService;
import net.consensys.web3auth.common.service.Web3AuthWSClient;

@Slf4j
public class AuthorisationFilter extends OncePerRequestFilter {
    
    private final ClientDetails client;
    private final String authorizationHeader;
    private final Web3AuthWSClient web3AuthWSClient;
    
    public AuthorisationFilter(String authEndpoint, ClientDetails client, String authorizationHeader, Web3AuthWSClient web3AuthWSClient) {
        this.client = client;
        this.authorizationHeader = authorizationHeader;
        this.web3AuthWSClient = web3AuthWSClient;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Extract token 
        Optional<String> token = null;
        if(client.getType().equals(ClientType.BROWSER)) {
            token = CookieService.getCookieValue(request, Constant.COOKIE_TOKEN_NAME);
            if(!token.isPresent()) {
                log.trace("No cookie {}", Constant.COOKIE_TOKEN_NAME);
                filterChain.doFilter(request, response);
                return;
            }
            
        } else if(client.getType().equals(ClientType.BEARER)) {
            String authenticationHeader = request.getHeader(authorizationHeader);
            log.trace("authenticationHeader={}", authenticationHeader);
            String authenticationParameter = decode(request.getParameter(authorizationHeader));
            log.trace("authenticationParameter={}", authenticationParameter);
    
            if (authenticationHeader != null && authenticationHeader.startsWith("Bearer ")) {
                token = Optional.of(authenticationHeader.substring(7));
                
            } else if (authenticationParameter != null && authenticationParameter.startsWith("Bearer ")) {
                token = Optional.of(authenticationParameter.substring(7));
                
            } else {
                log.trace("No header {}", authorizationHeader);
                log.trace("No parameter {}", authorizationHeader);
                filterChain.doFilter(request, response);
                return;
            }
            
        } else {
            log.error("unknow type {}", client.getType());
            filterChain.doFilter(request, response);
        }
        

        log.trace("token found = {}", token.get());
        
        // Validate token
        TokenDetails tokenDetails = this.web3AuthWSClient.validateToken(token.get());
        log.trace("tokenDetails = {}", tokenDetails);

        // Authorities
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        if(tokenDetails.getOrganisations() != null) {
            for(Organisation o: tokenDetails.getOrganisations()) {
                authorities.add(new SimpleGrantedAuthority(o.getName()));
                for(String p: o.getPrivileges()) {
                    authorities.add(new SimpleGrantedAuthority(p));
                }
            }
            
        }
        
        // Setup context
        AuthenticationToken authentication = new AuthenticationToken(tokenDetails.getAddress(), token.get(), authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("User {} authenticated!", tokenDetails.getAddress());

        // Next
        filterChain.doFilter(request, response);
    }
    
    private String decode(String value) throws UnsupportedEncodingException {
        return Optional.ofNullable(value).map( (v) -> {
            try {
                return URLDecoder.decode(v, StandardCharsets.UTF_8.toString());
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        }).orElse(null);
    }

}
