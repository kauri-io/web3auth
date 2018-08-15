/**
 * 
 */
package net.consensys.web3auth.module.login.service;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.common.dto.ClientType;
import net.consensys.web3auth.module.application.model.Application;
import net.consensys.web3auth.module.application.model.Application.Client;
import net.consensys.web3auth.module.application.service.ApplicationService;
import net.consensys.web3auth.module.common.CryptoUtils;
import net.consensys.web3auth.module.common.JwtUtils;
import net.consensys.web3auth.module.login.exception.SentenceExpiredException;
import net.consensys.web3auth.module.login.exception.SentenceNotFoundException;
import net.consensys.web3auth.module.login.exception.SignatureException;
import net.consensys.web3auth.module.login.exception.WrongClientTypeException;
import net.consensys.web3auth.module.login.model.LoginRequest;
import net.consensys.web3auth.module.login.model.LoginResponse;
import net.consensys.web3auth.module.login.model.LoginSentence;
import net.consensys.web3auth.module.login.service.ots.SentenceGeneratorService;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
@Service
@Slf4j
public class LoginServiceImpl implements LoginService{

    protected final SentenceGeneratorService sentenceGeneratorService;
    protected final ApplicationService applicationService;

    @Autowired
    protected LoginServiceImpl(SentenceGeneratorService sentenceGeneratorService, ApplicationService applicationService) {
        this.sentenceGeneratorService = sentenceGeneratorService;
        this.applicationService = applicationService;
    }

    @Override
    public LoginSentence init(String appId, String clientId, ClientType expectedClientType) {
        log.trace("init(appId: {}, clientId: {})", appId, clientId);

        Application application = this.getApplication(appId);
        Client client = this.getClient(appId, clientId);

        if(!client.getType().equals(expectedClientType)) {
            throw new WrongClientTypeException(clientId, client.getType(), expectedClientType);
        }
        
        return sentenceGeneratorService.generateSentence(application.getAppId(),
                client.getLoginSetting().getTimeout());
    }

    @Override
    public LoginResponse login(String appId, String clientId, ClientType expectedClientType, LoginRequest loginRequest) {
        log.trace("login(application: {}, client: {}, loginRequest: {})", appId, clientId, loginRequest);

        Application application = this.getApplication(appId);
        Client client = this.getClient(appId, clientId);

        if(!client.getType().equals(expectedClientType)) {
            throw new WrongClientTypeException(clientId, client.getType(), expectedClientType);
        }
        
        // Get Sentence
        Optional<LoginSentence> sentence = sentenceGeneratorService.getSentence(loginRequest.getSentenceId());
        if (!sentence.isPresent()) {
            throw new SentenceNotFoundException(loginRequest.getSentenceId());
        }
        if (!sentence.get().isActive()) {
            throw new SentenceExpiredException(loginRequest.getSentenceId());
        }

        // Check signature
        Map<Integer, String> addressesRecovered = CryptoUtils.ecrecover(loginRequest.getSignature(),
                sentence.get().getSentence());

        Optional<String> address = addressesRecovered.entrySet().stream()
                .filter(a -> loginRequest.getAddress().equals(a.getValue()))
                .map(Entry::getValue).findFirst();

        if (!address.isPresent()) {
            throw new SignatureException("Signature doesn't match");
        }
        
        // Generate JWT
        String token = JwtUtils.generateToken(application.getJwtSetting(), loginRequest.getAddress());

        // Disable the one-time sentence
        sentenceGeneratorService.disableSentence(loginRequest.getSentenceId());

        return new LoginResponse(loginRequest.getAppId(), loginRequest.getClientId(), loginRequest.getAddress(), token,
                JwtUtils.getExpirationDateFromToken(application.getJwtSetting(), token));

    }

    @Override
    public Client getClient(String appId, String clientId) {
        return applicationService.getClient(appId, clientId);
    }

    @Override
    public Application getApplication(String appId) {
        return applicationService.getApp(appId);
    }
    
}
