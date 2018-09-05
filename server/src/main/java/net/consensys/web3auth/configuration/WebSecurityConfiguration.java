package net.consensys.web3auth.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import net.consensys.web3auth.configuration.filter.CorsFilter;

/**
 * Configuration for security
 * 
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private CorsFilter corsFilter;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        
        httpSecurity 
          .csrf().disable()
          .exceptionHandling().authenticationEntryPoint(null)
          .and()
              .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          .and()
              .authorizeRequests()
              .anyRequest().anonymous()
          .and()
              .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class);
      }

}
