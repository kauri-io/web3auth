package net.consensys.web3auth.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.session.SessionManagementFilter;

import net.consensys.web3auth.common.Constant;
import net.consensys.web3auth.common.service.Web3AuthWSClient;
import net.consensys.web3auth.configuration.filter.CorsFilter;
import net.consensys.web3auth.module.adapter.springsecurity.Web3AuthSecurityConfiguration;

/**
 * Configuration for security
 * 
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends Web3AuthSecurityConfiguration {

    private static final String clientId = Constant.CLIENT_ID_SERVER;
    private static final String authEndpoint = "http://localhost:8080"; //TODO change by web3auth.serverURL
    private CorsFilter corsFilter;
    
    @Autowired
    public WebSecurityConfiguration(Web3AuthWSClient client) {
        super(clientId, authEndpoint, client);
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        super.configure(http);
        
        http.authorizeRequests()        
                .antMatchers(HttpMethod.POST, "/account/token").permitAll()
                .antMatchers(HttpMethod.GET, "/admin/**").permitAll() // find a better solution (some sort of machine2machine handshake)
                .antMatchers("/api/login").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/logout").permitAll()
                .antMatchers("/social-connect/**").permitAll()
                .antMatchers(HttpMethod.GET, "/static/**").permitAll()
                .antMatchers(HttpMethod.GET, "/manifest.json").permitAll()  
                .antMatchers(HttpMethod.GET, "/favicon.ico").permitAll()  
            .and()
            .authorizeRequests()
                .anyRequest().authenticated() //  anyRequest = antMatchers("/**")
            .and()
            .addFilterBefore(corsFilter, SessionManagementFilter.class);
    }

    @Bean
    public CorsFilter corsFilter(
            @Value("#{'${web3auth.cors.origins}'.split(',')}") List<String> allowedOrigin, 
            @Value("#{'${web3auth.cors.methods}'.split(',')}") List<String> allowedMethods, 
            @Value("#{'${web3auth.cors.headers}'.split(',')}") List<String> allowedHeaders, 
            @Value("${web3auth.cors.credentials}") boolean allowCredentials) {
        this.corsFilter = new CorsFilter(allowedOrigin, allowedMethods, allowedHeaders, allowCredentials);
        return this.corsFilter;
    }
}
