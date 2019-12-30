package net.consensys.web3auth.service.wallet;

public interface WalletProcessorService {
    
    void onKeyNewWallet(String address, String key);
    void onKeyAdded(String address, String key);
    void onKeyRemoved(String address, String key);
    
}
