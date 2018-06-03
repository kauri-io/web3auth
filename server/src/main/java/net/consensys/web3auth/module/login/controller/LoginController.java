package net.consensys.web3auth.module.login.controller;

import net.consensys.web3auth.module.application.model.Application;
import net.consensys.web3auth.module.application.model.Application.Client;
import net.consensys.web3auth.module.application.model.ApplicationException;
import net.consensys.web3auth.module.login.model.LoginRequest;
import net.consensys.web3auth.module.login.model.LoginResponse;
import net.consensys.web3auth.module.login.model.LoginSentence;
import net.consensys.web3auth.module.login.model.exception.LoginException;

public interface LoginController {
    
    LoginSentence init(Application application, Client client) throws LoginException, ApplicationException;
    
    LoginResponse login(Application application, Client client, LoginRequest loginRequest) throws LoginException, ApplicationException;
}
