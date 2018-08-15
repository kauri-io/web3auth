package net.consensys.web3auth.module.application.exception;

import lombok.Getter;

public class ApplicationException extends RuntimeException {

    private static final long serialVersionUID = 6532373444945616657L;

    private final @Getter String appId;
    
    public ApplicationException(String appId, String message) {
        super(message);
        this.appId = appId;
    }
    
    public ApplicationException(String appId, Throwable t) {
        super(t);
        this.appId = appId;
    }
}
