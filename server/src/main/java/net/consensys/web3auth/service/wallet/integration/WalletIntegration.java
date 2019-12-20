package net.consensys.web3auth.service.wallet.integration;

import java.math.BigInteger;
import java.util.List;
import java.util.function.BiConsumer;

public interface WalletIntegration {

    String deployWallet(String key);
    
    BigInteger getNonce(String wallet);
    
    List<String> getOwners(String wallet);
    
    String prepapreExec(String wallet, String to, String data);
    
    String exec(String wallet, String to, String data, String signature);

    void listenForNewWalletEvents(BiConsumer<String, String> onNewWallet);
    void listenForAddedOwnerEvents(List<String> addresses, BiConsumer<String, String> onOwnerAdded);
    void listenForRemovedOwnerEvents(List<String> addresses, BiConsumer<String, String> onOwnerRemoved);
}
