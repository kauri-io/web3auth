package net.consensys.web3auth.exception;

public class ContractException extends RuntimeException {

    private static final long serialVersionUID = 6146213075999164329L;

    public ContractException(String message, Throwable ex) {
        super(message, ex);
    }

    public ContractException(String message) {
        super(message);
    }
}
