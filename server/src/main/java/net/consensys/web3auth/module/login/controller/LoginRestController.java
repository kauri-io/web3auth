package net.consensys.web3auth.module.login.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.module.application.model.ApplicationException;
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
    public LoginSentence loginInit(
            @RequestParam(name="app_id", required = true) String appId,
            @RequestParam(name="client_id", required = true) String clientId) throws LoginException, ApplicationException {
        log.debug("loginInit(appId: {}, clientId: {})", appId, clientId);
         
        return super.init(appId, clientId);
    } 
    
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public LoginResponse login(@Valid final LoginRequest loginRequest, BindingResult result) throws LoginException, ApplicationException {
        log.debug("login(loginRequest: {})", loginRequest);

        return super.login(loginRequest, result);
    }

    
}
