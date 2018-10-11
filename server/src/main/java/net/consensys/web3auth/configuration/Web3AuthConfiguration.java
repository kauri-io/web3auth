/**
 * 
 */
package net.consensys.web3auth.configuration;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;
import net.consensys.web3auth.module.application.model.Application.AuthoritySetting;
import net.consensys.web3auth.module.application.model.Application.Client;
import net.consensys.web3auth.module.application.model.Application.CookieSetting;
import net.consensys.web3auth.module.application.model.Application.JwtSetting;
import net.consensys.web3auth.module.application.model.Application.OTSSetting;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
@Configuration
@ConfigurationProperties(prefix = "web3auth")
public class Web3AuthConfiguration {

    private @Getter @Setter String appId;
    private @Getter @Setter OTSSetting ots;
    private @Getter @Setter JwtSetting jwt;
    private @Getter @Setter CookieSetting cookie;
    private @Getter @Setter AuthoritySetting authority;
    private @Getter @Setter List<Client> clients;
    
}
