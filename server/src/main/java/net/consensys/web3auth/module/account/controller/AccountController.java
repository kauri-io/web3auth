package net.consensys.web3auth.module.account.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.common.dto.AccountDetails;
import net.consensys.web3auth.common.dto.Organisation;
import net.consensys.web3auth.module.application.model.Application;
import net.consensys.web3auth.module.application.service.ApplicationService;
import net.consensys.web3auth.module.common.JwtUtils;

@RestController
@RequestMapping("/account")
@Slf4j
public class AccountController {

    private final ApplicationService applicationService;

    @Autowired
    public AccountController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }
    
    
    @RequestMapping(value = "/token/{token}", method = RequestMethod.GET)
    public AccountDetails validateToken(
            @RequestParam(name="app_id", required = true) String appId, 
            @PathVariable String token) {
        
        log.debug("validateToken(appId: {}, token: {})", appId, token);
        
        Application application = applicationService.getApp(appId);

        JwtUtils.validateToken(application.getJwtSetting(), token);
        
        String address = JwtUtils.getUsernameFromToken(application.getJwtSetting(), token);

        return this.getAccountDetails(appId, address);
    } 
    
    @RequestMapping(value = "/address/{address}", method = RequestMethod.GET)
    public AccountDetails getAccountDetails(
            @RequestParam(name="app_id", required = true) String appId, 
            @PathVariable String address) {
        
        log.debug("getAccountDetails(appId: {}, address: {})", appId, address);
        
        Application application = applicationService.getApp(appId);

        List<Organisation> organisations = null;
        if(application.getAuthoritySetting().isEnable()) {
            organisations = applicationService.getAuthorityService(appId).getOrganisation(address);
        }
        
        return new AccountDetails(address, organisations);
    } 
}
