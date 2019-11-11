package net.consensys.web3auth.module.wallet.exception;

public class ContractException extends RuntimeException {

    private static final long serialVersionUID = 6146213075999164329L;

    public ContractException(String message, Throwable ex) {
        super(message, ex);
    }
}
