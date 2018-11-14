package net.consensys.web3auth.common.service;

import org.springframework.web.client.RestTemplate;

import net.consensys.web3auth.common.dto.AccountDetails;
import net.consensys.web3auth.common.dto.ClientDetails;
import net.consensys.web3auth.common.dto.exception.APIErrorHandler;

public class Web3AuthWSClientRestImpl implements Web3AuthWSClient {

    private final String authEndpoint;
    private final String appId;
    private final String clientId;
    private final RestTemplate restTemplate;
    
    public Web3AuthWSClientRestImpl(String endpoint, String appId, String clientId) {
        this.appId = appId;
        this.clientId = clientId;
        this.authEndpoint = endpoint;
        
        this.restTemplate = new RestTemplate();
        this.restTemplate.setErrorHandler(new APIErrorHandler());
    }
    
    @Override
    public ClientDetails getClient() {
        return restTemplate.getForObject(authEndpoint+"/application/"+appId+"?clientId="+clientId, ClientDetails.class);
    }

    @Override
    public AccountDetails getAccountByToken(String token, boolean getOrganisations) {
        return restTemplate.postForObject(authEndpoint+"/account/token?app_id="+appId+"&get_organisations=" + getOrganisations, token, AccountDetails.class);
    }

    @Override
    public AccountDetails getAccountByAddress(String address, boolean getOrganisations) {
        return restTemplate.getForObject(authEndpoint+"/account/address/"+address+"?app_id="+appId+"&get_organisations=" + getOrganisations, AccountDetails.class);
    }

}
