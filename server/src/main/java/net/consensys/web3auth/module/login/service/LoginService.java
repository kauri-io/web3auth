package net.consensys.web3auth.module.login.service;

import javax.servlet.http.HttpServletResponse;

import net.consensys.web3auth.common.dto.ClientType;
import net.consensys.web3auth.module.application.model.Application;
import net.consensys.web3auth.module.application.model.Application.Client;
import net.consensys.web3auth.module.login.model.LoginRequest;
import net.consensys.web3auth.module.login.model.LoginResponse;
import net.consensys.web3auth.module.login.model.LoginSentence;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public interface LoginService {

    Application getApplication(String appId);
    
    Client getClient(String appId, String clientId);
    
    LoginSentence init(String appId, String clientId, ClientType expectedClientType);
    
    LoginResponse login(String appId, String clientId, ClientType expectedClientType, LoginRequest loginRequest, HttpServletResponse response);
}
