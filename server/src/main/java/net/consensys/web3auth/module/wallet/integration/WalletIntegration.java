package net.consensys.web3auth.module.wallet.integration;

import net.consensys.web3auth.module.wallet.model.Key.KeyRole;

public interface WalletIntegration {

    String deployWallet(String key, String hash, String signature);
    void addKey(String wallet, String key, KeyRole role, String signature);
    void removeKey(String wallet, String key, String signature);
    Integer getNonce(String wallet);
    
}
