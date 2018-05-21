package net.consensys.web3auth.demo.client.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import net.consensys.web3auth.module.adapter.springsecurity.Web3AuthSecurityConfiguration;

@Configuration

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends Web3AuthSecurityConfiguration {

    private static final String appId = "demo";
    private static final String clientId = "demo_client";
    private static final String authEndpoint = "http://localhost:8080";

    public WebSecurityConfiguration() {
        super(appId, clientId, authEndpoint);
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        
        http 
            .authorizeRequests()
            .antMatchers(HttpMethod.GET, "/").permitAll()
            .antMatchers(HttpMethod.GET, "/public").permitAll()
            .anyRequest().authenticated();
    }
}
