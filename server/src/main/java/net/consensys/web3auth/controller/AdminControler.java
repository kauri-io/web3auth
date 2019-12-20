package net.consensys.web3auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.common.dto.ClientDetails;
import net.consensys.web3auth.configuration.Web3AuthSettings.Client;
import net.consensys.web3auth.service.admin.ConfigService;

@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminControler {

    private final ConfigService configService;
    
    public AdminControler(ConfigService configService) {
        this.configService = configService;
    }
    
    @GetMapping(value = "/config/{clientId}")
    public ClientDetails getClientDetails(
            @PathVariable String clientId) {
        log.debug("getClientDetails(clientId: {})", clientId);
        
        Client client = configService.getClient(clientId);

        return new ClientDetails(configService.getInstance(), client.getClientId(), client.getType(), client.getDefaultRedirect());
    } 
    
}
