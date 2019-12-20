package net.consensys.web3auth.service.socialconnect;

import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.client.RestTemplate;

import com.google.common.collect.ImmutableMap;

import net.consensys.web3auth.common.Constant;
import net.consensys.web3auth.common.CookieUtils;
import net.consensys.web3auth.common.CryptoUtils;
import net.consensys.web3auth.common.CryptoUtils.PrivateKeyAddress;
import net.consensys.web3auth.common.JwtUtils;
import net.consensys.web3auth.service.admin.ConfigService;
import net.consensys.web3auth.service.socialconnect.repository.SocialConnectAccount;
import net.consensys.web3auth.service.socialconnect.repository.SocialConnectAccountRepository;
import net.consensys.web3auth.service.socialconnect.repository.SocialConnectCSRFRepository;

public abstract class AbstractSocialConnectService {
    
    protected final ConfigService configService;
    protected final SocialConnectCSRFRepository socialConnectCSRFRepository;
    protected final SocialConnectAccountRepository socialConnectAccountRepository;
    protected final RestTemplate restTemplate;
    
   
    public AbstractSocialConnectService(ConfigService configService, SocialConnectAccountRepository socialConnectAccountRepository,
            SocialConnectCSRFRepository socialConnectCSRFRepository) {
        this.configService = configService;
        this.socialConnectAccountRepository = socialConnectAccountRepository;
        this.socialConnectCSRFRepository = socialConnectCSRFRepository;
        this.restTemplate = new RestTemplate();
    }
    
    protected String generateNewSocialConnectAccount(String email, String type, String clientId, String redirectURI, String handle, HttpServletResponse response) {
        
        Optional<SocialConnectAccount> account = socialConnectAccountRepository.findOneByEmail(email);
        
        final PrivateKeyAddress privateKeyAddress;
        
        if(!account.isPresent()) {
            privateKeyAddress = CryptoUtils.createNewWallet();
            
            socialConnectAccountRepository.save(new SocialConnectAccount(
                    privateKeyAddress.getAddress(), 
                    email, 
                    privateKeyAddress.getPrivateKey(), 
                    ImmutableMap.of(type, handle)));
        } else {
            privateKeyAddress = new PrivateKeyAddress(account.get().getPk(), account.get().getAccountId());
        }
        
        // generate session
        String token = JwtUtils.generateToken(configService.getJwt(), privateKeyAddress.getAddress());
        
        // set cookies
        CookieUtils.addCookie(configService.getCookie(), response, Constant.COOKIE_SOCIAL_CONNECT_TOKEN, token, JwtUtils.getExpirationDateFromToken(configService.getJwt(), token), true);
        CookieUtils.addCookie(configService.getCookie(), response, Constant.COOKIE_PROVIDER, Constant.PROVIDER_SOCIAL_CONNECT, JwtUtils.getExpirationDateFromToken(configService.getJwt(), token), false);

        return configService.getServerUrl() + "/login?client_id="+clientId+"&redirect_uri="+redirectURI;
    }
    
}
