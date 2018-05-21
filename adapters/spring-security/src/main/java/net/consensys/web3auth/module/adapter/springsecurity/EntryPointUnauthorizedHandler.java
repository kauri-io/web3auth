package net.consensys.web3auth.module.adapter.springsecurity;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.common.dto.ClientDetails;
import net.consensys.web3auth.common.dto.ClientType;

@Slf4j
public class EntryPointUnauthorizedHandler implements AuthenticationEntryPoint {

    private final String authEndpoint;
    private final ClientDetails client;
    
    public EntryPointUnauthorizedHandler(String authEndpoint, ClientDetails client) {
        this.authEndpoint = authEndpoint;
        this.client = client;
    }
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {

        if(client.getType().equals(ClientType.BROWSER)) {
            String uri = authEndpoint + "/login?app_id="+client.getAppId()+"&client_id="+client.getClientId()+"&redirect_uri="+request.getRequestURL();
            log.debug("redirect to {}", uri);
            response.sendRedirect(uri);
            
        } else if(client.getType().equals(ClientType.BEARER)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access Denied");
            
        } else {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unused type");
        }
    }

}