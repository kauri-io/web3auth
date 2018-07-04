package net.consensys.web3auth.module.login.controller.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.module.login.controller.LoginRestController;
import net.consensys.web3auth.module.login.model.exception.LoginException;
import net.consensys.web3auth.module.login.model.exception.LogoutException;

@ControllerAdvice(basePackageClasses=LoginRestController.class)
@Order(1)
@Slf4j
public class LoginRESTControllerExceptionHandler  {
    
    public LoginRESTControllerExceptionHandler() {
    }
    
    @ExceptionHandler(LoginException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleLoginException(HttpServletRequest req, HttpServletResponse resp, LoginException ex) throws LoginException {
      log.warn("Request: {} {} raised a LoginException: {}", req.getMethod(), req.getRequestURL(),  ex.getMessage());

      throw ex;
    }
    
    @ExceptionHandler(LogoutException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleLogoutException(HttpServletRequest req, LogoutException ex) throws LogoutException {
      log.warn("Request: {} {} raised a LogoutException {}", req.getMethod(),  req.getRequestURL(),  ex.getMessage());

      throw ex;
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleException(HttpServletRequest req, Exception ex) throws Exception  {
      log.error("Request: {} {} raised an unexpected Exception", req.getMethod(),  req.getRequestURL(),  ex);

      throw ex;
    }
}
