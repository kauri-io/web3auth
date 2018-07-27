package net.consensys.web3auth.common.service;

import net.consensys.web3auth.common.dto.ClientDetails;
import net.consensys.web3auth.common.dto.TokenDetails;

public interface Web3AuthWSClient {

    ClientDetails getClient();
    
    TokenDetails validateToken(String token);
}
