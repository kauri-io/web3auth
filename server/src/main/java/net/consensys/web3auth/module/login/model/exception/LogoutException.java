package net.consensys.web3auth.module.login.model.exception;

public class LogoutException extends Exception {

    private static final long serialVersionUID = 6532373444945616657L;

    private final String appId;
    private final String redirectUri;
    
    public LogoutException(String appId, String redirectUri, String message) {
        super(message);
        this.appId = appId;
        this.redirectUri = redirectUri;
    }
    
    public LogoutException(String appId, String redirectUri, Throwable t) {
        super(t);
        this.appId = appId;
        this.redirectUri = redirectUri;
    }

    public String getAppId() {
        return appId;
    }
    public String getRedirectUri() {
        return redirectUri;
    }
    
}
