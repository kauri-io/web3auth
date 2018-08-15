package net.consensys.web3auth.module.login.controller.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import net.consensys.web3auth.module.login.controller.LoginBrowserController;

@ControllerAdvice(basePackageClasses=LoginBrowserController.class)
@Order(2)
public class LoginBrowserControllerExceptionHandler  {
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleException(HttpServletRequest req, Exception ex) {
      return new ModelAndView("login").addObject("error", ex.getMessage());
    }
}
