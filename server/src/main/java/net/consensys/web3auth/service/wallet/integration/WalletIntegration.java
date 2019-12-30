package net.consensys.web3auth.service.wallet.integration;

import java.math.BigInteger;
import java.util.List;

public interface WalletIntegration {

    String getProxyAddress();
    
    String deployWallet(String key);
    
    BigInteger getNonce(String wallet);
    
    List<String> getOwners(String wallet);
    
    String prepapreExec(String wallet, String to, String data);
    
    String exec(String wallet, String to, String data, String signature);
}
