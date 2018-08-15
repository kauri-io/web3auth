package net.consensys.web3auth.module.application.exception;

import lombok.Getter;

public class ClientException extends RuntimeException {

    private static final long serialVersionUID = -3294340485651253923L;
    
    private final @Getter String clientId;
    
    public ClientException(String clientId, String message) {
        super(message);
        this.clientId = clientId;
    }
    
    public ClientException(String clientId, Throwable t) {
        super(t);
        this.clientId = clientId;
    }
}
