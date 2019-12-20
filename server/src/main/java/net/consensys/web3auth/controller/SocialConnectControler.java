package net.consensys.web3auth.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.common.Constant;
import net.consensys.web3auth.common.CookieUtils;
import net.consensys.web3auth.common.dto.SocialConnectGetAccount;
import net.consensys.web3auth.common.dto.SocialConnectSign;
import net.consensys.web3auth.exception.ForbiddenException;
import net.consensys.web3auth.service.socialconnect.SocialConnectGitHubService;
import net.consensys.web3auth.service.socialconnect.SocialConnectService;

@RestController
@RequestMapping("/social-connect")
@Slf4j
public class SocialConnectControler {

    private final SocialConnectGitHubService socialConnectGitHubService;
    private final SocialConnectService socialConnectService;
    
    public SocialConnectControler(SocialConnectGitHubService socialConnectGitHubService, SocialConnectService socialConnectService) {
        this.socialConnectService = socialConnectService;
        this.socialConnectGitHubService = socialConnectGitHubService;
    }
    
    @GetMapping(value = "/auth/github")
    public @ResponseBody ModelAndView githubConnect(
            @RequestParam(name="clientId", required = true) String clientId,
            @RequestParam(name="redirectUri", required = false) String redirectUri) {
        log.debug("githubConnect()");
        
        String authorizeUrl = socialConnectGitHubService.connect(clientId, redirectUri);
        
        return new ModelAndView(Constant.REDIRECT + authorizeUrl);
    } 
    
    @RequestMapping(value = "/auth/github/redirect", method = {RequestMethod.HEAD, RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE})
    public @ResponseBody ModelAndView githubRedirect(
            @RequestParam(name="code", required = true) String code,
            @RequestParam(name="state", required = true) String state,
            HttpServletResponse response) {
        log.debug("githubRedirect(code: {}, state: {})", code, state);

        String callbackURL = socialConnectGitHubService.redirect(code, state, response);
        
        return new ModelAndView(Constant.REDIRECT + callbackURL);
    } 
    

    @GetMapping(value = "/")
    public @ResponseBody SocialConnectGetAccount getAccount(HttpServletRequest request) {
        log.debug("getAccount()");
        
        Optional<String> token = CookieUtils.getCookieValue(request, Constant.COOKIE_SOCIAL_CONNECT_TOKEN);
        if(!token.isPresent()) {
            throw new ForbiddenException("No token passed");
        }
        
        return socialConnectService.getAccount(token.get());
    } 
    

    @PostMapping(value = "/sign")
    public @ResponseBody SocialConnectSign sign(@RequestBody SocialConnectSign body, HttpServletRequest request) {
        log.debug("sign(body: {})", body);
        
        Optional<String> token = CookieUtils.getCookieValue(request, Constant.COOKIE_SOCIAL_CONNECT_TOKEN);
        if(!token.isPresent()) {
            throw new ForbiddenException("No token passed");
        }
        
        body.setSignature(socialConnectService.sign(token.get(), body.getMessage()));
        
        return body;
    } 
    
}
