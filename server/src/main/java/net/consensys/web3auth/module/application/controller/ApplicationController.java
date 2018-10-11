package net.consensys.web3auth.module.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.common.dto.ClientDetails;
import net.consensys.web3auth.module.application.model.Application.Client;
import net.consensys.web3auth.module.application.service.ApplicationService;

@RestController
@RequestMapping("/application")
@Slf4j
public class ApplicationController {

    private final ApplicationService applicationService;

    @Autowired
    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }
    
    @RequestMapping(value = "/{appId}", method = RequestMethod.GET)
    public ClientDetails getApplicationDetails(@PathVariable String appId, @RequestParam(name="clientId", required = true) String clientId) {
        log.debug("getApplicationDetails(appId: {}, clientId: {})", appId, clientId);
        
        Client client = applicationService.getClient(clientId);

        return new ClientDetails(applicationService.getAppId(), client.getClientId(), client.getType());
    } 
    
}
