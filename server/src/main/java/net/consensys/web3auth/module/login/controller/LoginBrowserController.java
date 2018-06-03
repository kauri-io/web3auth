package net.consensys.web3auth.module.login.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.common.Constant;
import net.consensys.web3auth.common.dto.ClientType;
import net.consensys.web3auth.common.service.CookieService;
import net.consensys.web3auth.module.application.model.Application;
import net.consensys.web3auth.module.application.model.Application.Client;
import net.consensys.web3auth.module.application.model.ApplicationException;
import net.consensys.web3auth.module.application.service.ApplicationService;
import net.consensys.web3auth.module.login.model.LoginRequest;
import net.consensys.web3auth.module.login.model.LoginResponse;
import net.consensys.web3auth.module.login.model.LoginSentence;
import net.consensys.web3auth.module.login.model.exception.LoginException;
import net.consensys.web3auth.module.login.model.exception.LogoutException;
import net.consensys.web3auth.module.login.service.SentenceGeneratorService;
import net.consensys.web3auth.service.JwtService;

@Controller
@RequestMapping("/")
@Slf4j
public class LoginBrowserController extends LoginAbstractController {
    

    @Autowired
    public LoginBrowserController(SentenceGeneratorService sentenceGeneratorService, JwtService jwtService, ApplicationService applicationService) {
        super(sentenceGeneratorService, jwtService, applicationService);
    }
    
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView init(
            @RequestParam(name="app_id", required = true) String appId,
            @RequestParam(name="client_id", required = true) String clientId,
            @RequestParam(name="redirect_uri", required = false) String redirectUri, 
            String error, final ModelMap model,
            HttpServletRequest request) throws LoginException, ApplicationException {
        
        log.debug("loginPage(appId: {}, clientId:{}, redirectUri: {}, error: {})", appId, clientId, redirectUri, error);
        
        // Check if application exist
        Optional<Application> application = applicationService.getApp(appId);
        if(!application.isPresent()) {      
            throw new ApplicationException(appId, clientId, redirectUri, "Application [app: "+appId+"] doesn't exist");
        }
        
        Optional<Client> client = applicationService.getClient(appId, clientId);
        if(!client.isPresent()) {      
            throw new ApplicationException(appId, clientId, redirectUri, "Client [app: "+appId+", client: "+clientId+"] doesn't exist");
        }
        if(!client.get().getType().equals(ClientType.BROWSER)) {
            throw new ApplicationException(appId, clientId, redirectUri, "Client [app: "+appId+", client: "+clientId+"] has the wrong type, should be BROWSER");
        }
        
        // Generate and store random sentence
        LoginSentence sentence = super.init(application.get(), client.get());

        model.addAttribute("app_id", appId);
        model.addAttribute("redirect_uri", redirectUri);
        
        return new ModelAndView("login", model)
                .addObject("error", error)
                .addObject("sentence", sentence.getSentence())
                .addObject("loginRequest", new LoginRequest(appId, clientId, sentence.getId(), redirectUri));
    } 
    
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ModelAndView login(
            @Valid final LoginRequest loginRequest, 
            BindingResult result, final ModelMap model,
            HttpServletResponse response, HttpServletRequest request) throws LoginException, ApplicationException {
        
        log.debug("login(loginRequest: {})", loginRequest);

        // Check object
        if (result.hasErrors()) {
            throw new LoginException(loginRequest, "validation error");
        }
        
        // Check if application exist
        Optional<Application> application = applicationService.getApp(loginRequest.getAppId());
        if(!application.isPresent()) {      
            throw new ApplicationException(loginRequest.getAppId(), loginRequest.getClientId(), loginRequest.getRedirectUri(), "Application [app: "+loginRequest.getAppId()+"] doesn't exist");
        }
        
        // Check if client exist
        Optional<Client> client = applicationService.getClient(loginRequest.getAppId(), loginRequest.getClientId());
        if(!client.isPresent()) {      
            throw new ApplicationException(loginRequest.getAppId(), loginRequest.getClientId(), loginRequest.getRedirectUri(), "Client [app: "+loginRequest.getAppId()+", client: "+loginRequest.getClientId()+"] doesn't exist");
        }
        if(!client.get().getType().equals(ClientType.BROWSER)) {
            throw new ApplicationException(loginRequest.getAppId(), loginRequest.getClientId(), loginRequest.getRedirectUri(), "Client [app: "+loginRequest.getAppId()+", client: "+loginRequest.getClientId()+"] has the wrong type, should be BROWSER");
        }
        
        
        LoginResponse loginResponse = super.login(application.get(), client.get(), loginRequest);
        
        // Add cookies
        CookieService.addCookie(client.get().getLoginSetting().getCookieSetting(), response, Constant.COOKIE_TOKEN_NAME, loginResponse.getToken(), jwtService.getExpirationDateFromToken(application.get().getJwtSetting(), loginResponse.getToken()), true);
        CookieService.addCookie(client.get().getLoginSetting().getCookieSetting(), response, Constant.COOKIE_ADDRESS_NAME, loginRequest.getAddress(), jwtService.getExpirationDateFromToken(application.get().getJwtSetting(), loginResponse.getToken()), false);

        
        // Redirect
        if(StringUtils.isEmpty(loginRequest.getRedirectUri())) {
            return new ModelAndView("redirect:" + client.get().getUrl()); 
        } else {
            return new ModelAndView("redirect:" + loginRequest.getRedirectUri());
        }
    }
    
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ModelAndView logout(
            @RequestParam(name="app_id", required = true) String appId,
            @RequestParam(name="client_id", required = true) String clientId,
            @RequestParam(name="redirect_uri", required = false) String redirectUri, HttpServletResponse response, final ModelMap model) 
                    throws LogoutException, ApplicationException {
        
        // Check if app exist
        Optional<Client> client = applicationService.getClient(appId, clientId);
        if(!client.isPresent()) {
            throw new ApplicationException(appId, clientId, redirectUri, "Application [app: "+appId+", client: "+clientId+"] doesn't exist");
        }
        
        // Delete cookies
        CookieService.deleteCookie(client.get().getLoginSetting().getCookieSetting(), response, Constant.COOKIE_TOKEN_NAME);
        CookieService.deleteCookie(client.get().getLoginSetting().getCookieSetting(), response, Constant.COOKIE_ADDRESS_NAME);
        
        // Redirect
        if(StringUtils.isEmpty(redirectUri)) {
            return new ModelAndView("redirect:" + client.get().getUrl()); 
        } else {
            return new ModelAndView("redirect:" + redirectUri);
        }
    }
    
    @RequestMapping(value = "/success", method = RequestMethod.GET)
    public ModelAndView success(final ModelMap model) {
        
        log.debug("success()");
        
        return new ModelAndView("success", model);
    } 
    
}
