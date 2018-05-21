package net.consensys.web3auth.demo.client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PublicController {
    
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView indexPage(final ModelMap model) throws Exception {

        return new ModelAndView("index", model);
    } 

    @RequestMapping(value = "/public", method = RequestMethod.GET)
    public ModelAndView publicPage(final ModelMap model) throws Exception {

        return new ModelAndView("public", model);
    } 
    
    
}
