package net.consensys.web3auth.module.adapter.springsecurity;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import net.consensys.web3auth.common.dto.ClientDetails;
import net.consensys.web3auth.common.service.Web3AuthWSClient;
import net.consensys.web3auth.common.service.Web3AuthWSClientRestImpl;

public class Web3AuthSecurityConfiguration extends WebSecurityConfigurerAdapter {
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final ClientDetails client;
    private final String authorizationHeader;
    private final String authEndpoint;
    private final Web3AuthWSClient web3AuthWSClient;

    public Web3AuthSecurityConfiguration(String appId, String clientId, String authEndpoint) {
       this(appId, clientId, authEndpoint, AUTHORIZATION_HEADER);
    }
    
    public Web3AuthSecurityConfiguration(String appId, String clientId, String authEndpoint, String authorizationHeader) {
        this.authEndpoint = authEndpoint;
        this.authorizationHeader = authorizationHeader;
        this.web3AuthWSClient =  new Web3AuthWSClientRestImpl(authEndpoint, appId, clientId);
        this.client = this.web3AuthWSClient.getClient();
    }
    
    @Bean
    protected Web3AuthWSClient web3AuthWSClient() {
        return this.web3AuthWSClient; 
    }
    
    @Bean
    protected AuthenticationEntryPoint authenticationEntryPoint() {
        return new EntryPointUnauthorizedHandler(authEndpoint, client);
    }
    
    @Bean
    protected AuthorisationFilter authorisationFilter() {
        return new AuthorisationFilter(client, authorizationHeader, web3AuthWSClient);
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        
        http 
        .csrf().disable()
        .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
        .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
            .addFilterBefore(authorisationFilter(), BasicAuthenticationFilter.class);
    }
}
