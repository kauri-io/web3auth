package net.consensys.web3auth.module.wallet.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.web3j.utils.Numeric;

import net.consensys.web3auth.module.wallet.integration.WalletIntegration;
import net.consensys.web3auth.module.wallet.model.Key;
import net.consensys.web3auth.module.wallet.model.Key.KeyRole;
import net.consensys.web3auth.module.wallet.model.Wallet;
import net.consensys.web3auth.module.wallet.repository.WalletRepository;

@Service
@ConditionalOnProperty(name = "web3auth.wallet.enable", havingValue = "true")
public class DefaultWalletService implements WalletService {

    private final WalletIntegration integration;
    private final WalletRepository repository;
    
    public DefaultWalletService(WalletIntegration integration, WalletRepository repository) {
        this.integration = integration;
        this.repository = repository;
    }
    
    @Override
    public Wallet get(String address) {
        Optional<Wallet> found = repository.findById(address);
        
        if(!found.isPresent()) {
            throw new RuntimeException("No wallet found at address " + address);
        }
        
        Wallet wallet = found.get();
        wallet.setNonce(integration.getNonce(address));
        
        return wallet;
    }

    @Override
    public Wallet findByKey(String key) {
        
        List<Wallet> wallet = repository.findByKey(key);
        if(wallet.size() == 0) {
            throw new RuntimeException("No wallet found for key " + key);
        }
        return wallet.get(0);
    }

    @Override
    public Wallet addKey(String address, String key, KeyRole role, String signature) {
        Wallet wallet = get(address);
        
        integration.addKey(address, key, role, signature);
        
        wallet.getKeys().add(new Key(key, role));
        
        return repository.save(wallet);
    }

    @Override
    public Wallet removeKey(String address, String key, String signature) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Wallet create(String key, byte[] hash, String signature) {
        
        List<Wallet> found = repository.findByKey(key);
        if(found.size() > 0) {
            return found.get(0);
        }
        
        String address = integration.deployWallet(key, Numeric.toHexString(hash), signature);
        Wallet wallet = new Wallet(address, 0, Arrays.asList(new Key(key, KeyRole.MANAGEMENT)));
        
        repository.save(wallet);
        
        return wallet;
    }

}
