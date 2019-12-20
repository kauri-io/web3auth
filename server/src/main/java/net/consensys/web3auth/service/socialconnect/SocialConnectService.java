package net.consensys.web3auth.service.socialconnect;

import net.consensys.web3auth.common.dto.SocialConnectGetAccount;

public interface SocialConnectService {

    SocialConnectGetAccount getAccount(String token);
    String sign(String token, String data);
    
}
