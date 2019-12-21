package net.consensys.web3auth.demo.api.controller;

import java.security.Principal;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class SecureController {
    
    @RequestMapping(value = "/secure", method = RequestMethod.GET)
    public String secure(final Principal principal, final ModelMap model) throws Exception {
        log.debug("authenticated as {}", principal.getName());
        return "authenticated as " + principal.getName();
    } 
    
    
}
