package net.consensys.web3auth.module.account.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.common.dto.AccountDetails;
import net.consensys.web3auth.common.dto.Organisation;
import net.consensys.web3auth.configuration.Web3AuthConfiguration;
import net.consensys.web3auth.module.authority.service.AuthorityService;
import net.consensys.web3auth.module.common.JwtUtils;

@RestController
@RequestMapping("/account")
@Slf4j
public class AccountController {

    private final Web3AuthConfiguration web3AuthConfiguration;
    private final AuthorityService authorityService;
    
    @Autowired
    public AccountController(Web3AuthConfiguration web3AuthConfiguration, AuthorityService authorityService) {
        this.web3AuthConfiguration = web3AuthConfiguration;
        this.authorityService = authorityService;
    }
    
    
    @RequestMapping(value = "/token", method = RequestMethod.POST)
    public AccountDetails validateToken(
            @RequestParam(name="app_id", required = true) String appId, 
            @RequestParam(name="get_organisations", required = false, defaultValue="true") boolean getOrganisation, 
            @RequestBody String token) {
        
        log.debug("validateToken(appId: {}, getOrganisation: {}, token: {})", appId, getOrganisation, token);
        
        JwtUtils.validateToken(web3AuthConfiguration.getJwt(), token);
        
        String address = JwtUtils.getUsernameFromToken(web3AuthConfiguration.getJwt(), token);

        return this.getAccountDetails(appId, getOrganisation, address);
    } 
    
    @RequestMapping(value = "/address/{address}", method = RequestMethod.GET)
    public AccountDetails getAccountDetails(
            @RequestParam(name="app_id", required = true) String appId, 
            @RequestParam(name="get_organisations", required = false, defaultValue="true") boolean getOrganisation, 
            @PathVariable String address) {
        
        log.debug("getAccountDetails(appId: {}, getOrganisation: {}, address: {})", appId, getOrganisation, address);
        
        Set<Organisation> organisations = null;
        if(getOrganisation) {
            organisations = authorityService.getOrganisation(address);
        }
        
        AccountDetails account = new AccountDetails(address, organisations);
        log.debug("getAccountDetails(appId: {}, getOrganisation: {}, address: {}) {}", appId, getOrganisation, address, account);
 
        return account;
    } 
}
