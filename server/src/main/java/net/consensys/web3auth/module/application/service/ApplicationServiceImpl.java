package net.consensys.web3auth.module.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.consensys.web3auth.configuration.Web3AuthConfiguration;
import net.consensys.web3auth.module.application.exception.ClientNotFound;
import net.consensys.web3auth.module.application.model.Application.AuthoritySetting;
import net.consensys.web3auth.module.application.model.Application.Client;
import net.consensys.web3auth.module.application.model.Application.CookieSetting;
import net.consensys.web3auth.module.application.model.Application.JwtSetting;
import net.consensys.web3auth.module.application.model.Application.OTSSetting;

@Service
public class ApplicationServiceImpl implements ApplicationService {
    
    private final Web3AuthConfiguration configuration;
    
    @Autowired
    public ApplicationServiceImpl(Web3AuthConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String getAppId() {
        return configuration.getAppId();
    }
    @Override
    public JwtSetting getJwt() {
        return configuration.getJwt();
    }
    @Override
    public OTSSetting getOts() {
        return configuration.getOts();
    }
    @Override
    public AuthoritySetting getAuthority() {
        return configuration.getAuthority();
    }
    @Override
    public CookieSetting getCookie() {
        return configuration.getCookie();
    }
    @Override
    public Client getClient(String clientId) {
        return configuration
                .getClients()
                .stream()
                .filter(client -> client.getClientId().equals(clientId))
                .findFirst()
                .orElseThrow(() -> new ClientNotFound(clientId));
    }
    
}
