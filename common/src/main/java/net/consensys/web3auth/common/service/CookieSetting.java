package net.consensys.web3auth.common.service;

import lombok.Data;

@Data
public class CookieSetting {
    
    private String jwtCookie;
    private String userCookie;
    private String path;
    private String domain;
    private boolean secure;
    
}