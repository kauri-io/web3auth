package net.consensys.web3auth.service.wallet.integration.eventeum;

public interface BlockchainEventRegister {

    void registerAddedOwnerEvent(String address);
    void registerRemovedOwnerEvent(String address);
    void registerProxyCreationEvent(String address);
    
}
