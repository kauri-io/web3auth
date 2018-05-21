package net.consensys.web3auth.module.login.controller;

import java.util.Map;
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
import net.consensys.web3auth.common.service.CookieService;
import net.consensys.web3auth.module.application.model.Application;
import net.consensys.web3auth.module.application.model.Application.Client;
import net.consensys.web3auth.module.application.model.ApplicationException;
import net.consensys.web3auth.module.application.service.ApplicationService;
import net.consensys.web3auth.module.login.model.LoginRequest;
import net.consensys.web3auth.module.login.model.LoginSentence;
import net.consensys.web3auth.module.login.model.exception.LoginException;
import net.consensys.web3auth.module.login.model.exception.LogoutException;
import net.consensys.web3auth.module.login.service.SentenceGeneratorService;
import net.consensys.web3auth.service.JwtService;
import net.consensys.web3auth.service.crypto.CryptoUtils;

@Controller
@RequestMapping("/")
@Slf4j
public class LoginController {
    
    private final SentenceGeneratorService sentenceGeneratorService;
    private final JwtService jwtService;
    private final ApplicationService applicationService;
    
    @Autowired
    public LoginController(SentenceGeneratorService sentenceGeneratorService, JwtService jwtService, ApplicationService applicationService) {
        this.sentenceGeneratorService = sentenceGeneratorService;
        this.jwtService = jwtService;
        this.applicationService = applicationService;
    }
    
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView loginPage(
            @RequestParam(name="app_id", required = true) String appId,
            @RequestParam(name="client_id", required = true) String clientId,
            @RequestParam(name="redirect_uri", required = false) String redirectUri, 
            String error, final ModelMap model,
            HttpServletRequest request) throws LoginException, ApplicationException {
        
        log.debug("loginPage(appId: {}, redirectUri: {}, error: {})", appId, redirectUri, error);
        
        // Check if client exist
        Optional<Client> client = applicationService.getClient(appId, clientId);
        if(!client.isPresent()) {      
            throw new ApplicationException(appId, clientId, redirectUri, "Client [app: "+appId+", client: "+clientId+"] doesn't exist");
        }
            
        // Generate and store random sentence
        LoginSentence sentence = sentenceGeneratorService.generateSentence(appId, client.get().getLoginSetting().getTimeout());

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
            throw new LoginException(loginRequest.getAppId(), loginRequest.getClientId(), loginRequest.getRedirectUri(), "validation error");
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
        
        // Check Redirect URI
        if(!loginRequest.getRedirectUri().contains(client.get().getUrl())) {
            throw new LoginException(loginRequest.getAppId(), loginRequest.getClientId(), loginRequest.getRedirectUri(), "wrong redirect uri");
        }
        
        // Get Sentence
        LoginSentence sentence = sentenceGeneratorService.getSentencce(loginRequest.getSentenceId());
        if(sentence == null) {
            throw new LoginException(loginRequest.getAppId(), loginRequest.getClientId(), loginRequest.getRedirectUri(), "sentence not found");
        }
        if(!sentence.isActive()) {
            throw new LoginException(loginRequest.getAppId(), loginRequest.getClientId(), loginRequest.getRedirectUri(), "sentence disabled after timeout");
        }
        
        // Check signature
        Map<Integer, String> addressesRecovered = CryptoUtils.ecrecover(
                loginRequest.getSignature(), 
                sentence.getSentence());
        
        Optional<String> address = addressesRecovered.entrySet().stream()
                .filter(map -> loginRequest.getAddress().equals(map.getValue()))
                .map(map -> map.getValue())
                .findFirst();
        
        if(!address.isPresent()) {
            throw new LoginException(loginRequest.getAppId(), loginRequest.getClientId(), loginRequest.getRedirectUri(), "Signature doesn't match");
        }
        
        // Generate JWT
        String token = jwtService.generateToken(application.get().getJwtSetting(), loginRequest.getAddress());
        
        // Add cookies
        CookieService.addCookie(client.get().getLoginSetting().getCookieSetting(), response, Constant.COOKIE_TOKEN_NAME, token, jwtService.getExpirationDateFromToken(application.get().getJwtSetting(), token), true);
        CookieService.addCookie(client.get().getLoginSetting().getCookieSetting(), response, Constant.COOKIE_ADDRESS_NAME, loginRequest.getAddress(), jwtService.getExpirationDateFromToken(application.get().getJwtSetting(), token), false);

        // Disable the one-time sentence
        sentenceGeneratorService.disableSentence(loginRequest.getSentenceId());
        
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
