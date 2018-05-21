package net.consensys.web3auth.module.adapter.springsecurity;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.common.Constant;
import net.consensys.web3auth.common.dto.ClientDetails;
import net.consensys.web3auth.common.dto.ClientType;
import net.consensys.web3auth.common.dto.TokenDetails;
import net.consensys.web3auth.common.service.CookieService;

@Slf4j
public class AuthorisationFilter extends OncePerRequestFilter {
    private final static String AUTHORIZATION_HEADER = "Authorization";
    
    private final ClientDetails client;
    private final String authEndpoint;
    private final RestTemplate restTemplate;
    
    public AuthorisationFilter(String authEndpoint, ClientDetails client) {
        this.client = client;
        this.authEndpoint = authEndpoint;
        this.restTemplate = new RestTemplate();
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
            String authenticationHeader = request.getHeader(AUTHORIZATION_HEADER);
            
            if (authenticationHeader != null && authenticationHeader.startsWith("Bearer ")) {
                token = Optional.of(authenticationHeader.substring(7));
                
            } else {
                log.trace("No header {}", AUTHORIZATION_HEADER);
                filterChain.doFilter(request, response);
                return;
            }
            
        } else {
            log.error("unknow type {}", client.getType());
            filterChain.doFilter(request, response);
        }
        

        log.trace("token found = {}", token.get());
        
        // Validate token
        TokenDetails tokenDetails = restTemplate.postForObject(authEndpoint+"/admin/token?app_id="+client.getAppId(),  token.get(), TokenDetails.class);

        // Setup context
        AuthenticationToken authentication = new AuthenticationToken(tokenDetails.getAddress());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("User {} authenticated!", tokenDetails.getAddress());

        // Next
        filterChain.doFilter(request, response);
    }



}
