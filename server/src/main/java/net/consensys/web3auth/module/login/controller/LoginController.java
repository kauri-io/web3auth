package net.consensys.web3auth.module.login.controller;

import org.springframework.validation.BindingResult;

import net.consensys.web3auth.module.application.model.ApplicationException;
import net.consensys.web3auth.module.login.model.LoginRequest;
import net.consensys.web3auth.module.login.model.LoginResponse;
import net.consensys.web3auth.module.login.model.LoginSentence;
import net.consensys.web3auth.module.login.model.exception.LoginException;

public interface LoginController {
    
    LoginSentence init(String appId, String clientId) throws LoginException, ApplicationException;
    LoginResponse login(final LoginRequest loginRequest, BindingResult result) throws LoginException, ApplicationException;
}
