package net.consensys.web3auth.module.authority.exception;

public class SmartContractException extends RuntimeException {

    private static final long serialVersionUID = 3659968344333405147L;

    public SmartContractException(Throwable e) {
        super(e);
    }
    
    public SmartContractException(String m) {
        super(m);
    }
}
