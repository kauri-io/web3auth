package net.consensys.web3auth.service.socialconnect;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.ECKeyPair;
import org.web3j.utils.Numeric;

import net.consensys.web3auth.common.CryptoUtils;
import net.consensys.web3auth.common.JwtUtils;
import net.consensys.web3auth.common.dto.SocialConnectGetAccount;
import net.consensys.web3auth.service.admin.ConfigService;
import net.consensys.web3auth.service.socialconnect.repository.SocialConnectAccount;
import net.consensys.web3auth.service.socialconnect.repository.SocialConnectAccountRepository;

@Service
public class SocialConnectServiceImpl implements SocialConnectService {

    protected final ConfigService configService;
    private final SocialConnectAccountRepository socialConnectAccountRepository;
    
    @Autowired
    public SocialConnectServiceImpl(ConfigService configService, SocialConnectAccountRepository socialConnectAccountRepository) {
        this.configService = configService;
        this.socialConnectAccountRepository = socialConnectAccountRepository;
    }

    @Override
    public SocialConnectGetAccount getAccount(String token) {
        SocialConnectAccount socialConnectAccount = getSocialConnectAccount(token);
 
        return new SocialConnectGetAccount(socialConnectAccount.getAccountId(), socialConnectAccount.getEmail());
    }

    @Override
    public String sign(String token, String data) {
        SocialConnectAccount socialConnectAccount = getSocialConnectAccount(token);

        return CryptoUtils.sign(data, ECKeyPair.create(Numeric.hexStringToByteArray(socialConnectAccount.getPk())), true);
    }
    
    private SocialConnectAccount getSocialConnectAccount(String token) {
        JwtUtils.validateToken(configService.getJwt(), token);
        String account = JwtUtils.getUsernameFromToken(configService.getJwt(), token);
        
        Optional<SocialConnectAccount> socialConnectAccount = socialConnectAccountRepository.findById(account);
        if(!socialConnectAccount.isPresent()) {
            throw new RuntimeException("No account found with id " + account);
        }
        
        return socialConnectAccount.get();
    }
    

}
