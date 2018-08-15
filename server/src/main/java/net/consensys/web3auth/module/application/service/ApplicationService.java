package net.consensys.web3auth.module.application.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import net.consensys.web3auth.module.application.model.Application;
import net.consensys.web3auth.module.application.model.Application.Client;
import net.consensys.web3auth.module.application.exception.ApplicationNotFound;
import net.consensys.web3auth.module.application.exception.ClientNotFound;
import net.consensys.web3auth.module.authority.service.AuthorityService;
import net.consensys.web3auth.module.authority.service.SmartContractEventAuthorityService;
import net.consensys.web3auth.module.authority.service.SmartContractGetterAuthorityService;

@Service
@ConfigurationProperties(prefix = "web3-auth")
public class ApplicationService {
    
    private final String ethereumNodeUrl;
    private final List<Application> apps;
    
    @Autowired
    public ApplicationService(@Value("${ethereum.enable}") boolean ethereumEnable, 
            @Value("${ethereum.node.url}") String ethereumUrl) {
        if(ethereumEnable) {
            this.ethereumNodeUrl = ethereumUrl;
        } else {
            this.ethereumNodeUrl = null;
        }
        apps = new ArrayList<>();
    }

    public List<Application> getApps() {
        return apps;
    }
    
    public Application getApp(String appId) {
        return apps
                .stream()
                .filter(app -> app.getAppId().equals(appId))
                .findFirst()
                .orElseThrow(() -> new ApplicationNotFound(appId));
    }
    
    public Client getClient(String appId, String clientId) {
        return this.getApp(appId)
                .getClients()
                .stream()
                .filter(client -> client.getClientId().equals(clientId))
                .findFirst()
                .orElseThrow(() -> new ClientNotFound(appId));
    }
    
    public AuthorityService getAuthorityService(String appId) {
        Application application = this.getApp(appId);
        
        if(this.ethereumNodeUrl == null) {
            throw new IllegalArgumentException("Ethereum disable!");
        }
        
        Web3j web3j = Web3j.build(new HttpService(this.ethereumNodeUrl));

        if(application.getAuthoritySetting().isEnable()) {
            
            switch (application.getAuthoritySetting().getMode()) {
            case GETTER:
                return new SmartContractGetterAuthorityService(web3j, application.getAuthoritySetting().getSmartContract());
            case EVENT:
                return new SmartContractEventAuthorityService(web3j, application.getAuthoritySetting().getSmartContract());
            default:
                return null;
            }
            
        } else {
            return null;
        }
    }
 
}
