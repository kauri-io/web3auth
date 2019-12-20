/**
 * 
 */
package net.consensys.web3auth.service.login;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.common.Constant;
import net.consensys.web3auth.common.CookieUtils;
import net.consensys.web3auth.common.CryptoUtils;
import net.consensys.web3auth.common.JwtUtils;
import net.consensys.web3auth.common.dto.ClientType;
import net.consensys.web3auth.common.dto.LoginRequest;
import net.consensys.web3auth.common.dto.LoginResponse;
import net.consensys.web3auth.common.dto.OTC;
import net.consensys.web3auth.configuration.Web3AuthSettings.Client;
import net.consensys.web3auth.exception.OTCExpiredException;
import net.consensys.web3auth.exception.OTCNotFoundException;
import net.consensys.web3auth.exception.SignatureException;
import net.consensys.web3auth.exception.WrongClientTypeException;
import net.consensys.web3auth.service.admin.ConfigService;
import net.consensys.web3auth.service.login.otc.OTCGeneratorService;
import net.consensys.web3auth.service.wallet.WalletService;
import net.consensys.web3auth.service.wallet.repository.Wallet;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    private final OTCGeneratorService otcGeneratorService;
    private final ConfigService configService;
    private final WalletService walletService;

    @Autowired
    protected LoginServiceImpl(
            OTCGeneratorService otcGeneratorService, 
            ConfigService configService,
            @Lazy WalletService walletService) {
        this.otcGeneratorService = otcGeneratorService;
        this.configService = configService;
        this.walletService = walletService;
    }

    @Override
    public OTC init(String clientId, Collection<ClientType> expectedClientType) {
        log.trace("init(clientId: {})", clientId);

        Client client = this.getClient(clientId);

        if(!expectedClientType.contains(client.getType())) {
            throw new WrongClientTypeException(clientId, client.getType(), expectedClientType);
        }
        
        return otcGeneratorService.generateOTC(configService.getInstance());
    }

    @Override
    public LoginResponse login(String clientId, Collection<ClientType> expectedClientType, LoginRequest loginRequest, HttpServletResponse response) {
        log.trace("login(client: {}, loginRequest: {})", clientId, loginRequest);

        Client client = this.getClient(clientId);

        if(!expectedClientType.contains(client.getType())) {
            throw new WrongClientTypeException(clientId, client.getType(), expectedClientType);
        }
        
        // Get Sentence
        Optional<OTC> otc = otcGeneratorService.getOTC(loginRequest.getOtcId());
        if (!otc.isPresent()) {
            throw new OTCNotFoundException(loginRequest.getOtcId());
        }
        if (!otc.get().isActive()) {
            throw new OTCExpiredException(loginRequest.getOtcId());
        }

        // Check signature
        Map<Integer, String> addressesRecovered = CryptoUtils.ecrecover(loginRequest.getSignature(),
                otc.get().getCode());

        Optional<String> address = addressesRecovered.entrySet().stream()
                .filter(a -> loginRequest.getAccount().equalsIgnoreCase(a.getValue()))
                .map(Entry::getValue).findFirst();

        if (!address.isPresent()) {
            throw new SignatureException("Signature doesn't match");
        }
        
        // Deploy wallet
        Wallet wallet = walletService.create(loginRequest.getAccount());
        String walletAddress = wallet.getAddress();
        
        // Generate JWT
        String token = JwtUtils.generateToken(
                configService.getJwt(), 
                loginRequest.getAccount(), 
                ImmutableMap.of(
                        Constant.CLAIM_WALLET, walletAddress, 
                        Constant.CLAIM_ACCOUNT, loginRequest.getAccount()));

        // Disable the one-time sentence
        otcGeneratorService.disableOTC(loginRequest.getOtcId());

        // Set cookies
        CookieUtils.addCookie(configService.getCookie(), response, Constant.COOKIE_TOKEN, token, JwtUtils.getExpirationDateFromToken(configService.getJwt(), token), true);
        CookieUtils.addCookie(configService.getCookie(), response, Constant.COOKIE_ACCOUNT, loginRequest.getAccount(), JwtUtils.getExpirationDateFromToken(configService.getJwt(), token), false);
        CookieUtils.addCookie(configService.getCookie(), response, Constant.COOKIE_WALLET, walletAddress, JwtUtils.getExpirationDateFromToken(configService.getJwt(), token), false);
        CookieUtils.addCookie(configService.getCookie(), response, Constant.COOKIE_PROVIDER, loginRequest.getProvider(), JwtUtils.getExpirationDateFromToken(configService.getJwt(), token), false);

        return new LoginResponse(loginRequest.getClientId(), loginRequest.getAccount(), walletAddress, token,
                JwtUtils.getExpirationDateFromToken(configService.getJwt(), token));

    }

    @Override
    public Client getClient(String clientId) {
        return configService.getClient(clientId);
    }

}
