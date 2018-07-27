package net.consensys.web3auth.module.admin.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.common.dto.ClientDetails;
import net.consensys.web3auth.common.dto.Organisation;
import net.consensys.web3auth.common.dto.TokenDetails;
import net.consensys.web3auth.module.application.model.Application;
import net.consensys.web3auth.module.application.model.Application.Client;
import net.consensys.web3auth.module.application.service.ApplicationService;
import net.consensys.web3auth.module.authority.Authority;
import net.consensys.web3auth.service.JwtService;

@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    private final JwtService jwtService;
    private final ApplicationService applicationService;
    private final Authority authority;

    
    @Autowired
    public AdminController(JwtService jwtService, ApplicationService applicationService, Authority authority) {
        this.jwtService = jwtService;
        this.applicationService = applicationService;
        this.authority = authority;
    }
    
    @RequestMapping(value = "/token", method = RequestMethod.POST)
    public TokenDetails validateToken(@RequestParam(name="app_id", required = true) String appId, @RequestBody String token) throws Exception {
        
        log.debug("validateToken(appId: {}, token: {})", appId, token);
        
        Optional<Application> application = applicationService.getApp(appId);
        if(!application.isPresent()) {
            throw new Exception("Application doesn't exist");
        }
        
        if(!jwtService.validateToken(application.get().getJwtSetting(), token)) {
            throw new Exception("Invalid token");
        }
        
        String address = jwtService.getUsernameFromToken(application.get().getJwtSetting(), token);
        List<Organisation> organisations = null;
        if(application.get().getAuthoritySetting().isEnable()) {
            organisations = authority
                .getOrganisations(application.get().getAuthoritySetting().getSmartContract(), address)
                .stream()
                .map((name) -> new Organisation(name, authority.getPrivileges(application.get().getAuthoritySetting().getSmartContract(), address, name)))
                .collect(Collectors.toList());
        }
        
        return new TokenDetails(address, organisations);
    } 

    @RequestMapping(value = "/application/{appId}/client/{clientId}", method = RequestMethod.GET)
    public ClientDetails getApplicationDetails(@PathVariable String appId, @PathVariable String clientId) throws Exception {
        
        log.debug("getApplicationDetails(appId: {}, clientId: {})", appId, clientId);
        
        Optional<Application> application = applicationService.getApp(appId);
        if(!application.isPresent()) {
            //TODO 404
            throw new Exception("Application ["+appId+"]  doesn't exist");
        }
        
        Optional<Client> client = applicationService.getClient(appId, clientId);
        if(!client.isPresent()) {
            //TODO 404
            throw new Exception("client ["+clientId+"] doesn't exist");
        }
        
        return new ClientDetails(application.get().getAppId(), client.get().getClientId(), client.get().getType());
    } 
    
}
