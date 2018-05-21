package net.consensys.web3auth.module.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import net.consensys.web3auth.module.application.model.Application;
import net.consensys.web3auth.module.application.model.Application.Client;
import net.consensys.web3auth.module.application.model.ApplicationException;

@Service
@ConfigurationProperties(prefix = "web3-auth")
public class ApplicationService {

    private final List<Application> apps;
    
    @Autowired
    public ApplicationService() {
        apps = new ArrayList<>();
    }

    public List<Application> getApps() {
        return apps;
    }
    
    public Optional<Application> getApp(String appId) {
        return apps.stream().filter(app -> app.getAppId().equals(appId)).findFirst();
    }
    
    public Optional<Client> getClient(String appId, String clientId) throws ApplicationException {
        Optional<Application> application = this.getApp(appId);
        if(!application.isPresent()) {
            throw new ApplicationException(appId, clientId, null, "Application " + appId + " not found!");
        }

        return application.get().getClients().stream().filter(app -> app.getClientId().equals(clientId)).findFirst();
    }
 
}
