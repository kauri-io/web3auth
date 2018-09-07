package net.consensys.web3auth.common.service;

import net.consensys.web3auth.common.dto.ClientDetails;
import net.consensys.web3auth.common.dto.AccountDetails;

public interface Web3AuthWSClient {

    ClientDetails getClient();
    
    AccountDetails getAccountByToken(String token, boolean getOrganisations);
    
    AccountDetails getAccountByAddress(String address, boolean getOrganisations);
}
