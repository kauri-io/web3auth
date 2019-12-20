package net.consensys.web3auth.exception;

public class ClientNotFound extends ClientException {

    private static final long serialVersionUID = 5444700869619220917L;

    public ClientNotFound(String clientId) {
        super(clientId, "Client [id: "+clientId+"] not found");
    }
}
