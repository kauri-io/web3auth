package net.consensys.web3auth.exception;

public class ForbiddenException extends RuntimeException {

    private static final long serialVersionUID = -3294340485651253923L;
    
    public ForbiddenException(String message) {
        super(message);
    }
    
    public ForbiddenException(String message, Throwable t) {
        super(message, t);
    }
}
