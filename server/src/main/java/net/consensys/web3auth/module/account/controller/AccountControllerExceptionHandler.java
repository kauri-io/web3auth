package net.consensys.web3auth.module.account.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.common.dto.exception.APIErrorDetails;
import net.consensys.web3auth.module.common.exception.TokenException;

@RestControllerAdvice(basePackageClasses=AccountController.class)
@Slf4j
public class AccountControllerExceptionHandler  {
    
    @ExceptionHandler(TokenException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<APIErrorDetails> handleTokenException(HttpServletRequest req, HttpServletResponse resp, TokenException ex)  {
        log.warn("Token exception [token: {}]: {}", ex.getToken(), ex.getMessage());
        
        APIErrorDetails err = new APIErrorDetails(ex.getMessage(), ex.getToken());
        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<APIErrorDetails> handleException(HttpServletRequest req, Exception ex) {
        log.error("Unexpected exception: {}", ex.getMessage(), ex);
        
        APIErrorDetails err = new APIErrorDetails(ex.getMessage(), null);
        return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
