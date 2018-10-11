/**
 * 
 */
package net.consensys.web3auth.module.login.service;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.common.Constant;
import net.consensys.web3auth.common.dto.ClientType;
import net.consensys.web3auth.module.application.model.Application.Client;
import net.consensys.web3auth.module.application.service.ApplicationService;
import net.consensys.web3auth.module.common.CookieUtils;
import net.consensys.web3auth.module.common.CryptoUtils;
import net.consensys.web3auth.module.common.JwtUtils;
import net.consensys.web3auth.module.login.exception.SentenceExpiredException;
import net.consensys.web3auth.module.login.exception.SentenceNotFoundException;
import net.consensys.web3auth.module.login.exception.SignatureException;
import net.consensys.web3auth.module.login.exception.WrongClientTypeException;
import net.consensys.web3auth.module.login.model.LoginRequest;
import net.consensys.web3auth.module.login.model.LoginResponse;
import net.consensys.web3auth.module.login.model.OTS;
import net.consensys.web3auth.module.login.service.ots.OTSGeneratorService;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    private final OTSGeneratorService otsGeneratorService;
    private final ApplicationService applicationService;

    @Autowired
    protected LoginServiceImpl(
            OTSGeneratorService otsGeneratorService, 
            ApplicationService applicationService) {
        this.otsGeneratorService = otsGeneratorService;
        this.applicationService = applicationService;
    }

    @Override
    public OTS init(String clientId, ClientType expectedClientType) {
        log.trace("init(clientId: {})", clientId);

        Client client = this.getClient(clientId);

        if(!client.getType().equals(expectedClientType)) {
            throw new WrongClientTypeException(clientId, client.getType(), expectedClientType);
        }
        
        return otsGeneratorService.generateOTS(applicationService.getAppId());
    }

    @Override
    public LoginResponse login(String clientId, ClientType expectedClientType, LoginRequest loginRequest, HttpServletResponse response) {
        log.trace("login(client: {}, loginRequest: {})", clientId, loginRequest);

        Client client = this.getClient(clientId);

        if(!client.getType().equals(expectedClientType)) {
            throw new WrongClientTypeException(clientId, client.getType(), expectedClientType);
        }
        
        // Get Sentence
        Optional<OTS> sentence = otsGeneratorService.getOTS(loginRequest.getOtsId());
        if (!sentence.isPresent()) {
            throw new SentenceNotFoundException(loginRequest.getOtsId());
        }
        if (!sentence.get().isActive()) {
            throw new SentenceExpiredException(loginRequest.getOtsId());
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
        String token = JwtUtils.generateToken(applicationService.getJwt(), loginRequest.getAddress());

        // Disable the one-time sentence
        otsGeneratorService.disableOTS(loginRequest.getOtsId());

        // Set cookies
        CookieUtils.addCookie(applicationService.getCookie(), response, Constant.COOKIE_TOKEN_NAME, token, JwtUtils.getExpirationDateFromToken(applicationService.getJwt(), token), true);
        CookieUtils.addCookie(applicationService.getCookie(), response, Constant.COOKIE_ADDRESS_NAME, loginRequest.getAddress(), JwtUtils.getExpirationDateFromToken(applicationService.getJwt(), token), false);

        return new LoginResponse(loginRequest.getClientId(), loginRequest.getAddress(), token,
                JwtUtils.getExpirationDateFromToken(applicationService.getJwt(), token));

    }

    @Override
    public Client getClient(String clientId) {
        return applicationService.getClient(clientId);
    }

}
