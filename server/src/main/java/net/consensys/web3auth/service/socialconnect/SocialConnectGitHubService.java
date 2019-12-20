package net.consensys.web3auth.service.socialconnect;

import javax.servlet.http.HttpServletResponse;

public interface SocialConnectGitHubService {

    String connect(String clientId, String redirectUri);
    String redirect(String code, String state, HttpServletResponse response);
    
}
