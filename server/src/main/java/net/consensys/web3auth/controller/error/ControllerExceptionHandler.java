package net.consensys.web3auth.controller.error;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.common.dto.exception.APIErrorDetails;
import net.consensys.web3auth.exception.InstanceNotFound;
import net.consensys.web3auth.exception.ClientNotFound;
import net.consensys.web3auth.exception.ForbiddenException;
import net.consensys.web3auth.exception.OTCExpiredException;
import net.consensys.web3auth.exception.OTCNotFoundException;
import net.consensys.web3auth.exception.SignatureException;
import net.consensys.web3auth.exception.TokenException;
import net.consensys.web3auth.exception.WrongClientTypeException;

@RestControllerAdvice(basePackages="net.consensys.web3auth.controller")
@Slf4j
public class ControllerExceptionHandler  {

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ModelAndView handleException(HttpServletRequest req, HttpServletResponse resp, ForbiddenException ex)  {
        throw ex;
    }
    @ExceptionHandler(InstanceNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleException(HttpServletRequest req, HttpServletResponse resp, InstanceNotFound ex)  {
        throw ex;
    }
    @ExceptionHandler(ClientNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleException(HttpServletRequest req, HttpServletResponse resp, ClientNotFound ex)  {
        throw ex;
    }
    @ExceptionHandler(OTCNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleException(HttpServletRequest req, HttpServletResponse resp, OTCNotFoundException ex)  {
        throw ex;
    }
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleException(HttpServletRequest req, HttpServletResponse resp, ValidationException ex)  {
        throw ex;
    }
    @ExceptionHandler(OTCExpiredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleException(HttpServletRequest req, HttpServletResponse resp, OTCExpiredException ex)  {
        throw ex;
    }
    @ExceptionHandler(SignatureException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleException(HttpServletRequest req, HttpServletResponse resp, SignatureException ex)  {
        throw ex;
    }
    @ExceptionHandler(WrongClientTypeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleException(HttpServletRequest req, HttpServletResponse resp, WrongClientTypeException ex)  {
        throw ex;
    }
    
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
