package net.consensys.web3auth.module.login.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.common.dto.ClientType;
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
    public LoginSentence init(String appId, String clientId) throws LoginException, ApplicationException {
        
        // Check if client exist
        Optional<Client> client = applicationService.getClient(appId, clientId);
        if(!client.isPresent()) {      
            throw new ApplicationException(appId, clientId, null, "Client [app: "+appId+", client: "+clientId+"] doesn't exist");
        }
            
        // Generate and store random sentence
        LoginSentence sentence = sentenceGeneratorService.generateSentence(appId, client.get().getLoginSetting().getTimeout());
        
        return sentence;
    }

    @Override
    public LoginResponse login(final LoginRequest loginRequest, BindingResult result) throws LoginException, ApplicationException {

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
        
        // Check Redirect URI
        if(client.get().getType().equals(ClientType.BROWSER) && !loginRequest.getRedirectUri().contains(client.get().getUrl())) {
            throw new LoginException(loginRequest, "wrong redirect uri");
        }
        
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
        String token = jwtService.generateToken(application.get().getJwtSetting(), loginRequest.getAddress());
        
        // Disable the one-time sentence
        sentenceGeneratorService.disableSentence(loginRequest.getSentenceId());
        
        return new LoginResponse(
                loginRequest.getAppId(), 
                loginRequest.getClientId(), 
                loginRequest.getAddress(), 
                token, 
                jwtService.getExpirationDateFromToken(application.get().getJwtSetting(), token));
        
    }

}
