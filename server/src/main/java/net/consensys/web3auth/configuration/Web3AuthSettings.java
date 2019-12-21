/**
 * 
 */
package net.consensys.web3auth.configuration;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.consensys.web3auth.common.dto.ClientType;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
@Configuration
@ConfigurationProperties(prefix = "web3auth")
@Getter @Setter
public class Web3AuthSettings {

    private String instance;
    private String serverUrl;
    
    private OTCSetting otc;
    private JwtSetting jwt;
    private CookieSetting cookie;
    private List<Client> clients;
    private WalletSetting wallet;
    private SocialConnect socialConnect;

   @Data
   public static class OTCSetting {
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
   public static class SocialConnect {
       private GitHub github;
   }

   @Data
   public static class GitHub {
       private String clientId;
       private String clientSecret; 
   }
   
   @Data
   public static class WalletSetting {
       public enum WalletMode { RELAY, DIRECT}
       
       private WalletMode mode;
       private WalletDirect direct;
       private WalletViaRelay relay; 
   }

   @Data
   public static class WalletDirect {
       private String mnemonic;
       private String rpcUrl; 
       private String proxyFactory;
       private String gnosisSafeMasterCopy;
   }

   @Data
   public static class WalletViaRelay {
       private String endpoint;
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
       private String defaultRedirect;
   } 

}
