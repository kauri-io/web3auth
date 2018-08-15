package net.consensys.web3auth.module.login.controller.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import net.consensys.web3auth.module.application.exception.ApplicationNotFound;
import net.consensys.web3auth.module.application.exception.ClientNotFound;
import net.consensys.web3auth.module.login.controller.LoginRestController;
import net.consensys.web3auth.module.login.exception.SentenceExpiredException;
import net.consensys.web3auth.module.login.exception.SentenceNotFoundException;
import net.consensys.web3auth.module.login.exception.SignatureException;
import net.consensys.web3auth.module.login.exception.UnexpectedException;
import net.consensys.web3auth.module.login.exception.WrongClientTypeException;

@ControllerAdvice(basePackageClasses=LoginRestController.class)
@Order(1)
public class LoginRESTControllerExceptionHandler  {

    @ExceptionHandler(ApplicationNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleException(HttpServletRequest req, HttpServletResponse resp, ApplicationNotFound ex)  {
        throw ex;
    }
    @ExceptionHandler(ClientNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleException(HttpServletRequest req, HttpServletResponse resp, ClientNotFound ex)  {
        throw ex;
    }
    @ExceptionHandler(SentenceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleException(HttpServletRequest req, HttpServletResponse resp, SentenceNotFoundException ex)  {
        throw ex;
    }
    @ExceptionHandler(SentenceExpiredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleException(HttpServletRequest req, HttpServletResponse resp, SentenceExpiredException ex)  {
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
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleException(HttpServletRequest req, Exception ex) {
      throw new UnexpectedException(ex);
    }
}
