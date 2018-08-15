package net.consensys.web3auth.module.application.exception;

public class ApplicationNotFound extends ApplicationException {

    private static final long serialVersionUID = 5444700869619220917L;

    public ApplicationNotFound(String appId) {
        super(appId, "Application [id: "+appId+"] not found");
    }
}
