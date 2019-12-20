package net.consensys.web3auth.service.wallet.repository;

import java.util.List;

public interface CustomWalletRepository {
    
    List<Wallet> findByKey(String key);
    
}
