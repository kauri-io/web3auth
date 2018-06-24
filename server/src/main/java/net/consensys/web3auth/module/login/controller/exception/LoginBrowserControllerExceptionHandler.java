package net.consensys.web3auth.module.login.controller.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.module.application.model.ApplicationException;
import net.consensys.web3auth.module.login.controller.LoginBrowserController;
import net.consensys.web3auth.module.login.model.exception.LoginException;
import net.consensys.web3auth.module.login.model.exception.LogoutException;

@ControllerAdvice(basePackageClasses=LoginBrowserController.class)
@Slf4j
public class LoginBrowserControllerExceptionHandler  {
    
    private final LoginBrowserController loginBrowserController;
    
    public LoginBrowserControllerExceptionHandler(LoginBrowserController loginController) {
        this.loginBrowserController = loginController;
    }
    
    @ExceptionHandler(LoginException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleLoginException(HttpServletRequest req, HttpServletResponse resp, LoginException ex) throws LoginException, ApplicationException {
      log.warn("Request: {} {} raised a LoginException: {}", req.getMethod(), req.getRequestURL(),  ex.getMessage());

      return loginBrowserController.init(ex.getAppId(), ex.getClientId(),  ex.getRedirectUri(), ex.getMessage(), new ModelMap(), req);
    }
    
    @ExceptionHandler(LogoutException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleLogoutException(HttpServletRequest req, LogoutException ex) {
      log.warn("Request: {} {} raised a LogoutException {}", req.getMethod(),  req.getRequestURL(),  ex.getMessage());

      return new ModelAndView("redirect:" + ex.getRedirectUri());
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleException(HttpServletRequest req, Exception ex) {
      log.error("Request: {} {} raised an unexpected Exception", req.getMethod(),  req.getRequestURL(),  ex);
      
      return new ModelAndView("login").addObject("error", ex.getMessage());
    }
}
