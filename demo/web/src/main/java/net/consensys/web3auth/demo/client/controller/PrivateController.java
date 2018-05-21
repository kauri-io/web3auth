package net.consensys.web3auth.demo.client.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class PrivateController {
    
    @RequestMapping(value = "/private", method = RequestMethod.GET)
    public ModelAndView privatePage(final Principal principal, final ModelMap model) throws Exception {
        log.debug("authenticated as {}", principal.getName());
        return new ModelAndView("private", model);
    } 
    
    
}
