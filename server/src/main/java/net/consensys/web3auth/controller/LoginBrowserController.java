package net.consensys.web3auth.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.common.Constant;
import net.consensys.web3auth.common.CookieUtils;
import net.consensys.web3auth.common.dto.ClientType;
import net.consensys.web3auth.common.dto.LoginRequest;
import net.consensys.web3auth.common.dto.OTC;
import net.consensys.web3auth.configuration.Web3AuthSettings.Client;
import net.consensys.web3auth.service.admin.ConfigService;
import net.consensys.web3auth.service.login.LoginService;

@Controller
@RequestMapping("/")
@Slf4j
public class LoginBrowserController {
    private static final Collection<ClientType> EXPECTED_TYPES = Arrays.asList(ClientType.BROWSER, ClientType.BOTH);

    private final ConfigService configService;
    private final LoginService loginService;

    @Autowired
    public LoginBrowserController(LoginService loginService, ConfigService configService) {
        this.loginService = loginService;
        this.configService = configService;
    }

    @GetMapping(value = "/login")
    public ModelAndView init(@RequestParam(name = "client_id", required = true) String clientId,
            @RequestParam(name = "redirect_uri", required = false) String redirectUri, final ModelMap model) {

        log.debug("loginPage(clientId:{}, redirectUri: {})", clientId, redirectUri);

        // Generate and store random sentence
        OTC otc = loginService.init(clientId, EXPECTED_TYPES);

        model.addAttribute("redirect_uri", redirectUri);

        return new ModelAndView("index", model).addObject("code", otc.getCode()).addObject("loginRequest",
                new LoginRequest(clientId, otc.getId(), redirectUri));
    }

    @PostMapping(value = "/login")
    public ModelAndView login(@Valid final LoginRequest loginRequest, BindingResult result, final ModelMap model,
            HttpServletResponse response) {

        log.debug("login(loginRequest: {})", loginRequest);

        // Check object
        if (result.hasErrors()) {
            throw new ValidationException("validation error");
        }

        loginService.login(loginRequest.getClientId(), EXPECTED_TYPES, loginRequest, response);

        // Redirect
        if (StringUtils.isEmpty(loginRequest.getRedirectUri())) {
            Client client = this.loginService.getClient(loginRequest.getClientId());

            return new ModelAndView(Constant.REDIRECT + client.getDefaultRedirect());
        } else {
            return new ModelAndView(Constant.REDIRECT + loginRequest.getRedirectUri());
        }
    }

    @GetMapping(value = "/logout")
    public ModelAndView logout(Principal principal, @RequestParam(name = "client_id", required = true) String clientId,
            @RequestParam(name = "redirect_uri", required = false) String redirectUri, HttpServletRequest request,
            HttpServletResponse response, final ModelMap model) {

        Client client = loginService.getClient(clientId);

        // Delete cookies
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                CookieUtils.deleteCookie(configService.getCookie(), response, cookie.getName());
            }
        }

        // Redirect
        if (StringUtils.isEmpty(redirectUri)) {
            return new ModelAndView(Constant.REDIRECT + client.getDefaultRedirect());
        } else {
            return new ModelAndView(Constant.REDIRECT + redirectUri);
        }
    }

}
