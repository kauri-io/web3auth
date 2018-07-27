package net.consensys.web3auth.common.service;

import org.springframework.web.client.RestTemplate;

import net.consensys.web3auth.common.dto.ClientDetails;
import net.consensys.web3auth.common.dto.TokenDetails;

public class Web3AuthWSClientRestImpl implements Web3AuthWSClient {

    private String authEndpoint;
    private String appId;
    private String clientId;
    private final RestTemplate restTemplate;
    
    public Web3AuthWSClientRestImpl(String endpoint, String appId, String clientId) {
        this.appId = appId;
        this.clientId = clientId;
        this.authEndpoint = endpoint;
        this.restTemplate = new RestTemplate();
    }
    
    @Override
    public ClientDetails getClient() {
        return restTemplate.getForObject(authEndpoint+"/admin/application/"+appId+"/client/"+clientId, ClientDetails.class);
    }

    @Override
    public TokenDetails validateToken(String token) {
        return restTemplate.postForObject(authEndpoint+"/admin/token?app_id="+appId, token, TokenDetails.class);
    }

}
