package net.consensys.web3auth.service.wallet;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.service.wallet.repository.Wallet;
import net.consensys.web3auth.service.wallet.repository.WalletRepository;

@Service
@Slf4j
public class DefaultWalletProcessorService implements WalletProcessorService {

    private final WalletRepository repository;
    
    public DefaultWalletProcessorService(WalletRepository repository) {
        this.repository = repository;
    }
    

    @Override
    public void onKeyNewWallet(String address, String key) {
        log.debug("onKeyNewWallet(address: {}, key: {})", address, key);

        // Check if not already saved by method `create` (in case someone directly create the wallet via the proxy contract)
        Optional<Wallet> wallet = repository.findById(address);
        if(!wallet.isPresent()) {
            log.debug("Wallet {} doesn't exist, creating it", address);
            repository.save(new Wallet(address, 0, Arrays.asList()));
        }

        // Add the key
        onKeyAdded(address, key);
    }

    @Override
    public void onKeyAdded(String address, String key) {
        log.debug("onKeyAdded(address: {}, key: {})", address, key);

        Wallet wallet = get(address);
        if (!wallet.getKeys().contains(key)) {
            wallet.getKeys().add(key);
            repository.save(wallet);
        }
    }

    @Override
    public void onKeyRemoved(String address, String key) {
        log.debug("onKeyRemoved(address: {}, key: {})", address, key);

        Wallet wallet = get(address);
        if (wallet.getKeys().contains(key)) {
            wallet.getKeys().remove(key);
            repository.save(wallet);
        }
    }
    
    private Wallet get(String address) {
        Optional<Wallet> found = repository.findById(address);

        if (!found.isPresent()) {
            throw new RuntimeException("No wallet found at address " + address);
        }

        return found.get();
    }
}
