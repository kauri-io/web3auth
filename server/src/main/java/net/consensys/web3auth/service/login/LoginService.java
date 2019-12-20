package net.consensys.web3auth.service.login;

import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import net.consensys.web3auth.common.dto.ClientType;
import net.consensys.web3auth.common.dto.LoginRequest;
import net.consensys.web3auth.common.dto.LoginResponse;
import net.consensys.web3auth.common.dto.OTC;
import net.consensys.web3auth.configuration.Web3AuthSettings.Client;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public interface LoginService {

    Client getClient(String clientId);
    
    OTC init(String clientId, Collection<ClientType> expectedClientType);
    
    LoginResponse login(String clientId, Collection<ClientType> expectedClientType, LoginRequest loginRequest, HttpServletResponse response);
}
