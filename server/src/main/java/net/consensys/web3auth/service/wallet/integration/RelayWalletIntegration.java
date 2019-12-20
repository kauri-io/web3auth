package net.consensys.web3auth.service.wallet.integration;

import java.math.BigInteger;
import java.util.List;
import java.util.function.BiConsumer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "web3auth.wallet.mode", havingValue = "RELAy")
public class RelayWalletIntegration implements WalletIntegration {

    public RelayWalletIntegration() {
        throw new UnsupportedOperationException("mode RELAY not implemented yet...");
    }

    @Override
    public String deployWallet(String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BigInteger getNonce(String wallet) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getOwners(String wallet) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String prepapreExec(String wallet, String to, String data) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String exec(String wallet, String to, String data, String signature) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void listenForNewWalletEvents(BiConsumer<String, String> onNewWallet) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void listenForAddedOwnerEvents(List<String> addresses, BiConsumer<String, String> onOwnerAdded) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void listenForRemovedOwnerEvents(List<String> addresses, BiConsumer<String, String> onOwnerRemoved) {
        // TODO Auto-generated method stub
        
    }


    
}
