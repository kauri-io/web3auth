package net.consensys.web3auth.service.wallet;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.service.wallet.integration.WalletIntegration;
import net.consensys.web3auth.service.wallet.repository.Wallet;
import net.consensys.web3auth.service.wallet.repository.WalletRepository;

@Service
@Slf4j
public class DefaultWalletService implements WalletService {

    private final WalletIntegration integration;
    private final WalletRepository repository;
    
    public DefaultWalletService(WalletIntegration integration, WalletRepository repository) {
        this.integration = integration;
        this.repository = repository;
        
        integration.listenForNewWalletEvents((wallet, owner) -> {
            if(!repository.findById(wallet).isPresent()) {
                repository.save(new Wallet(wallet, 0, Arrays.asList()));
            }
            this.onKeyAdded(wallet, owner);
        });
        startEventListeners();
    }
    
    private void startEventListeners() {
        List<String> wallets = this.findAllWallets().stream()
                .map(Wallet::getAddress).collect(Collectors.toList());
                
        integration.listenForAddedOwnerEvents(wallets, 
                (wallet, owner) -> this.onKeyAdded(wallet, owner));
        
        integration.listenForRemovedOwnerEvents(wallets, 
                (wallet, owner) -> this.onKeyRemoved(wallet, owner));
    }
    
    @Override
    public Wallet get(String address) {
        Optional<Wallet> found = repository.findById(address);
        
        if(!found.isPresent()) {
            throw new RuntimeException("No wallet found at address " + address);
        }
        
        Wallet wallet = found.get();
        wallet.setNonce(integration.getNonce(address).intValue());
        
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
    public List<Wallet> findAllWallets() {
        return repository.findAll();
    }

    @Override
    public Wallet create(String key) {
        log.debug("create(key: {})", key);
        
        List<Wallet> found = repository.findByKey(key);

        if(found.size() > 0) {
            return found.get(0);
        }
        
        String address = integration.deployWallet(key);
        Wallet wallet = new Wallet(address, 0, Arrays.asList());
        
        repository.save(wallet);
        
        // Restart event listeners
        startEventListeners();
        
        return wallet;
    }

    @Override
    public String prepareExec(String address, String to, String data) {
        // TODO should log the call in the DB
        return integration.prepapreExec(address, to, data);
    }

    @Override
    public String exec(String address, String to, String data, String signature) {
        // TODO should log the call in the DB
        return integration.exec(address, to, data, signature);
    }

    @Override
    public void onKeyAdded(String address, String key) {
        log.debug("onKeyAdded(address: {}, key: {})", address, key);
        
        Wallet wallet = get(address);
        
        if(!wallet.getKeys().contains(key)) {
            wallet.getKeys().add(key);
            repository.save(wallet);
        }
    }

    @Override
    public void onKeyRemoved(String address, String key) {
        log.debug("onKeyRemoved(address: {}, key: {})", address, key);
        
        Wallet wallet = get(address);
        
        if(wallet.getKeys().contains(key)) {
            wallet.getKeys().remove(key);
            repository.save(wallet);
        }
    }

}
