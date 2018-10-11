package net.consensys.web3auth.module.login.controller;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.common.dto.ClientType;
import net.consensys.web3auth.module.login.model.LoginRequest;
import net.consensys.web3auth.module.login.model.LoginResponse;
import net.consensys.web3auth.module.login.model.OTS;
import net.consensys.web3auth.module.login.service.LoginService;

@RestController
@RequestMapping("/api/login")
@Slf4j
public class LoginRestController {
    
    private final LoginService loginService; 
    
    @Autowired
    public LoginRestController(LoginService loginService) {
        this.loginService = loginService;
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody OTS init(
            @RequestParam(name="client_id", required = true) String clientId) {
        log.debug("init(clientId: {})",  clientId);
        
        return loginService.init(clientId, ClientType.BEARER);
    } 
    
    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody LoginResponse login(@RequestBody final LoginRequest loginRequest, 
            BindingResult result, HttpServletResponse response) {
        log.debug("login(loginRequest: {})", loginRequest);

        // Check object
        if (result.hasErrors()) {
            throw new ValidationException("validation error");
        }
        
        return loginService.login(loginRequest.getClientId(), ClientType.BEARER, loginRequest, response);
    }

    
}
