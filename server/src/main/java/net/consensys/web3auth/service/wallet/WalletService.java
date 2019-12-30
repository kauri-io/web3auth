package net.consensys.web3auth.service.wallet;

import java.util.List;

import net.consensys.web3auth.service.wallet.repository.Wallet;

public interface WalletService {

    Wallet get(String address);
    Wallet findByKey(String key);
    List<Wallet> findAllWallets();
    Wallet create(String key);
    String prepareExec(String address, String to, String data);
    String exec(String address, String to, String data, String signature);
}
