package net.consensys.web3auth.module.login.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.module.application.model.Application;
import net.consensys.web3auth.module.application.model.Application.Client;
import net.consensys.web3auth.module.application.model.ApplicationException;
import net.consensys.web3auth.module.application.service.ApplicationService;
import net.consensys.web3auth.module.login.model.LoginRequest;
import net.consensys.web3auth.module.login.model.LoginResponse;
import net.consensys.web3auth.module.login.model.LoginSentence;
import net.consensys.web3auth.module.login.model.exception.LoginException;
import net.consensys.web3auth.module.login.service.SentenceGeneratorService;
import net.consensys.web3auth.service.JwtService;
import net.consensys.web3auth.service.crypto.CryptoUtils;

@Slf4j
public abstract class LoginAbstractController implements LoginController {
    
    protected final SentenceGeneratorService sentenceGeneratorService;
    protected final JwtService jwtService;
    protected final ApplicationService applicationService;
    
    @Autowired
    protected LoginAbstractController(SentenceGeneratorService sentenceGeneratorService, JwtService jwtService, ApplicationService applicationService) {
        this.sentenceGeneratorService = sentenceGeneratorService;
        this.jwtService = jwtService;
        this.applicationService = applicationService;
    }
    
    @Override
    public LoginSentence init(Application application, Client client) throws LoginException, ApplicationException {
        log.trace("init(application: {}, client: {})", application, client);
        
        LoginSentence sentence = sentenceGeneratorService.generateSentence(application.getAppId(), client.getLoginSetting().getTimeout());
        
        return sentence;
    }

    @Override
    public LoginResponse login(Application application, Client client, LoginRequest loginRequest) throws LoginException, ApplicationException {
        log.trace("login(application: {}, client: {}, loginRequest: {})", application, client, loginRequest);
        
        // Get Sentence
        LoginSentence sentence = sentenceGeneratorService.getSentencce(loginRequest.getSentenceId());
        if(sentence == null) {
            throw new LoginException(loginRequest, "sentence not found");
        }
        if(!sentence.isActive()) {
            throw new LoginException(loginRequest, "sentence disabled after timeout");
        }
        
        // Check signature
        Map<Integer, String> addressesRecovered = CryptoUtils.ecrecover(
                loginRequest.getSignature(), 
                sentence.getSentence());
        
        Optional<String> address = addressesRecovered.entrySet().stream()
                .filter(map -> loginRequest.getAddress().equals(map.getValue()))
                .map(map -> map.getValue())
                .findFirst();
        
        if(!address.isPresent()) {
            throw new LoginException(loginRequest.getAppId(), loginRequest.getClientId(), loginRequest.getRedirectUri(), "Signature doesn't match");
        }
        
        // Generate JWT
        String token = jwtService.generateToken(application.getJwtSetting(), loginRequest.getAddress());
        
        // Disable the one-time sentence
        sentenceGeneratorService.disableSentence(loginRequest.getSentenceId());
        
        return new LoginResponse(
                loginRequest.getAppId(), 
                loginRequest.getClientId(), 
                loginRequest.getAddress(), 
                token, 
                jwtService.getExpirationDateFromToken(application.getJwtSetting(), token));
        
    }

}
