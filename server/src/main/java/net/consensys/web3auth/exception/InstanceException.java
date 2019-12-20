package net.consensys.web3auth.exception;

import lombok.Getter;

public class InstanceException extends RuntimeException {

    private static final long serialVersionUID = 6532373444945616657L;

    private final @Getter String instance;
    
    public InstanceException(String instance, String message) {
        super(message);
        this.instance = instance;
    }
    
    public InstanceException(String instance, Throwable t) {
        super(t);
        this.instance = instance;
    }
}
