package net.consensys.web3auth.module.application.model;

import java.util.List;

import lombok.Data;
import lombok.ToString;
import net.consensys.web3auth.common.dto.ClientType;

@Data
@ToString
public class Application {

    private String appId;
    private OTSSetting ots;
    private JwtSetting jwt;
    private CookieSetting cookie;
    private AuthoritySetting authority;
    private List<Client> clients;
       
    @Data
    public static class OTSSetting {
        private String dictionnary;
        private int length;
        private String prefix;
        private long expiration;
    }
    
    @Data
    public static class JwtSetting {
        private String issuer;
        private String secret;
        private Long expiration;
    }

    @Data
    public static class AuthoritySetting {
        public enum SmartContractMode {NONE, GETTER, EVENT, CACHE}
        
        private SmartContractMode mode;
        private String ethereum;
        private String smartContract; 
        private String kafkaAddress;
        private String groupId;
        private String topic;
    }
    
    @Data
    public static class CookieSetting {
        private String jwtCookie;
        private String userCookie;
        private String path;
        private String domain;
        private boolean secure;
    }
    
    
    @Data
    public static class Client {
        private String clientId;
        private ClientType type;
        private String url;
    } 
}
