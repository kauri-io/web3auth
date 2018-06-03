package net.consensys.web3auth.module.login.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.common.dto.ClientType;
import net.consensys.web3auth.module.application.model.Application;
import net.consensys.web3auth.module.application.model.ApplicationException;
import net.consensys.web3auth.module.application.model.Application.Client;
import net.consensys.web3auth.module.application.service.ApplicationService;
import net.consensys.web3auth.module.login.model.LoginRequest;
import net.consensys.web3auth.module.login.model.LoginResponse;
import net.consensys.web3auth.module.login.model.LoginSentence;
import net.consensys.web3auth.module.login.model.exception.LoginException;
import net.consensys.web3auth.module.login.service.SentenceGeneratorService;
import net.consensys.web3auth.service.JwtService;

@Controller
@RequestMapping("/api")
@Slf4j
public class LoginRestController extends LoginAbstractController {
    

    @Autowired
    public LoginRestController(SentenceGeneratorService sentenceGeneratorService, JwtService jwtService, ApplicationService applicationService) {
        super(sentenceGeneratorService, jwtService, applicationService);
    }
    
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public LoginSentence init(
            @RequestParam(name="app_id", required = true) String appId,
            @RequestParam(name="client_id", required = true) String clientId) throws LoginException, ApplicationException {
        
        log.debug("loginInit(appId: {}, clientId: {})", appId, clientId);
        
        // Check if application exist
        Optional<Application> application = applicationService.getApp(appId);
        if(!application.isPresent()) {      
            throw new ApplicationException(appId, clientId, null, "Application [app: "+appId+"] doesn't exist");
        }
        
        Optional<Client> client = applicationService.getClient(appId, clientId);
        if(!client.isPresent()) {      
            throw new ApplicationException(appId, clientId, null, "Client [app: "+appId+", client: "+clientId+"] doesn't exist");
        }
        if(!client.get().getType().equals(ClientType.BEARER)) {
            throw new ApplicationException(appId, clientId, null, "Client [app: "+appId+", client: "+clientId+"] has the wrong type, should be BEARER");
        }
        
        return super.init(application.get(), client.get());
    } 
    
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public LoginResponse login(@Valid final LoginRequest loginRequest, BindingResult result) throws LoginException, ApplicationException {
        log.debug("login(loginRequest: {})", loginRequest);

        // Check object
        if (result.hasErrors()) {
            throw new LoginException(loginRequest, "validation error");
        }
        
        // Check if application exist
        Optional<Application> application = applicationService.getApp(loginRequest.getAppId());
        if(!application.isPresent()) {      
            throw new ApplicationException(loginRequest.getAppId(), loginRequest.getClientId(), loginRequest.getRedirectUri(), "Application [app: "+loginRequest.getAppId()+"] doesn't exist");
        }
        
        // Check if client exist
        Optional<Client> client = applicationService.getClient(loginRequest.getAppId(), loginRequest.getClientId());
        if(!client.isPresent()) {      
            throw new ApplicationException(loginRequest.getAppId(), loginRequest.getClientId(), loginRequest.getRedirectUri(), "Client [app: "+loginRequest.getAppId()+", client: "+loginRequest.getClientId()+"] doesn't exist");
        }
        if(!client.get().getType().equals(ClientType.BEARER)) {
            throw new ApplicationException(loginRequest.getAppId(), loginRequest.getClientId(), loginRequest.getRedirectUri(), "Client [app: "+loginRequest.getAppId()+", client: "+loginRequest.getClientId()+"] has the wrong type, should be BEARER");
        }
        
        
        return super.login(application.get(), client.get(), loginRequest);
    }

    
}
