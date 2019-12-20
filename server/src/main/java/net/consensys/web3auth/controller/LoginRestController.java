package net.consensys.web3auth.controller;

import java.util.Arrays;
import java.util.Collection;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.common.dto.ClientType;
import net.consensys.web3auth.common.dto.LoginRequest;
import net.consensys.web3auth.common.dto.LoginResponse;
import net.consensys.web3auth.common.dto.OTC;
import net.consensys.web3auth.service.login.LoginService;

@RestController
@RequestMapping("/api/login")
@Slf4j
public class LoginRestController {
    private static final Collection<ClientType> EXPECTED_TYPES = Arrays.asList(ClientType.BEARER, ClientType.BOTH);
    private final LoginService loginService; 
    
    @Autowired
    public LoginRestController(LoginService loginService) {
        this.loginService = loginService;
    }
    
    @GetMapping
    public @ResponseBody OTC init(
            @RequestParam(name="client_id", required = true) String clientId) {
        log.debug("init(clientId: {})",  clientId);
        
        return loginService.init(clientId, EXPECTED_TYPES);
    } 
    
    @PostMapping
    public @ResponseBody LoginResponse login(@RequestBody final LoginRequest loginRequest, 
            BindingResult result, HttpServletResponse response) {
        log.debug("login(loginRequest: {})", loginRequest);

        // Check object
        if (result.hasErrors()) {
            throw new ValidationException("validation error");
        }
        
        return loginService.login(loginRequest.getClientId(), EXPECTED_TYPES, loginRequest, response);
    }

    
}
