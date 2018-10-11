package net.consensys.web3auth.module.login.service;

import javax.servlet.http.HttpServletResponse;

import net.consensys.web3auth.common.dto.ClientType;
import net.consensys.web3auth.module.application.model.Application.Client;
import net.consensys.web3auth.module.login.model.LoginRequest;
import net.consensys.web3auth.module.login.model.LoginResponse;
import net.consensys.web3auth.module.login.model.OTS;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public interface LoginService {

    Client getClient(String clientId);
    
    OTS init(String clientId, ClientType expectedClientType);
    
    LoginResponse login(String clientId, ClientType expectedClientType, LoginRequest loginRequest, HttpServletResponse response);
}
