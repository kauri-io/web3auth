package net.consensys.web3auth.exception;

public class ContractDeploymentException extends RuntimeException {

    private static final long serialVersionUID = 6146213075999164329L;

    public ContractDeploymentException(String contract, Throwable ex) {
        super("Error while deploying contract " + contract, ex);
    }
}
