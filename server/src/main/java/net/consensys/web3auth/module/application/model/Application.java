package net.consensys.web3auth.module.application.model;

import java.util.List;

import lombok.Data;
import lombok.ToString;
import net.consensys.web3auth.common.dto.ClientType;
import net.consensys.web3auth.common.service.CookieSetting;

@Data
@ToString
public class Application {

    private String appId;
    private JwtSetting jwtSetting;
    private AuthoritySetting authoritySetting;
    private List<Client> clients;
    
    @Data
    public static class AuthoritySetting {
        private boolean enable;
        private String smartContract;
    }
    
    
    @Data
    public static class JwtSetting {
        private String issuer;
        private String secret;
        private Long expiration;
    }
    
    @Data
    public static class Client {
        private String clientId;
        private ClientType type;
        private String url;
        private Loginetting loginSetting;
    } 
    
    @Data
    public static class Loginetting {
        private Long timeout;
        private CookieSetting cookieSetting;
    }
    
}
