package net.consensys.web3auth.exception;

public class ConnectionException extends RuntimeException {

    private static final long serialVersionUID = 6146213075999164329L;

    public ConnectionException(String message, Throwable ex) {
        super(message, ex);
    }
}
