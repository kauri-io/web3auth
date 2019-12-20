package net.consensys.web3auth.exception;

public class InstanceNotFound extends InstanceException {

    private static final long serialVersionUID = 5444700869619220917L;

    public InstanceNotFound(String instance) {
        super(instance, "Instance [name: "+instance+"] not found");
    }
}
