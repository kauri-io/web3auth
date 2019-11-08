package net.consensys.web3auth.module.wallet.service;

import net.consensys.web3auth.module.wallet.model.Key.KeyRole;
import net.consensys.web3auth.module.wallet.model.Wallet;

public interface WalletService {

    Wallet get(String address);
    Wallet findByKey(String key);
    Wallet addKey(String address, String key, KeyRole role, String signature);
    Wallet removeKey(String address, String key, String signature);
    Wallet create(String key, byte[] hash, String signature);
}
