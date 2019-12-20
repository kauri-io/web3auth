package net.consensys.web3auth.common.service;

import org.springframework.web.client.RestTemplate;

import net.consensys.web3auth.common.dto.AccountDetails;
import net.consensys.web3auth.common.dto.ClientDetails;
import net.consensys.web3auth.common.dto.exception.APIErrorHandler;

public class Web3AuthWSClientRestImpl implements Web3AuthWSClient {

    private final String authEndpoint;
    private final String clientId;
    private final RestTemplate restTemplate;
    
    public Web3AuthWSClientRestImpl(String endpoint, String clientId) {
        this.clientId = clientId;
        this.authEndpoint = endpoint;
        
        this.restTemplate = new RestTemplate();
        this.restTemplate.setErrorHandler(new APIErrorHandler());
    }
    
    @Override
    public ClientDetails getClient() {
        return restTemplate.getForObject(authEndpoint+"/admin/config/"+clientId, ClientDetails.class);
    }

    @Override
    public AccountDetails getAccountByToken(String token) {
        return restTemplate.postForObject(authEndpoint+"/account/token", token, AccountDetails.class);
    }

}
