package net.consensys.web3auth.service.wallet;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.service.wallet.integration.WalletIntegration;
import net.consensys.web3auth.service.wallet.integration.eventeum.BlockchainEventRegister;
import net.consensys.web3auth.service.wallet.repository.Wallet;
import net.consensys.web3auth.service.wallet.repository.WalletRepository;

@Service
@Slf4j
public class DefaultWalletService implements WalletService {

    private final WalletIntegration integration;
    private final WalletRepository repository;
    private final BlockchainEventRegister eventRegister;

    public DefaultWalletService(WalletIntegration integration, WalletRepository repository, BlockchainEventRegister eventRegister) {
        this.integration = integration;
        this.repository = repository;
        this.eventRegister = eventRegister;
        
        this.eventRegister.registerProxyCreationEvent(integration.getProxyAddress());
    }


    @Override
    public Wallet get(String address) {
        Optional<Wallet> found = repository.findById(address);

        if (!found.isPresent()) {
            throw new RuntimeException("No wallet found at address " + address);
        }

        Wallet wallet = found.get();
        wallet.setNonce(integration.getNonce(address).intValue());

        return wallet;
    }

    @Override
    public Wallet findByKey(String key) {

        List<Wallet> wallet = repository.findByKey(key);
        if (wallet.size() == 0) {
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

        if (found.size() > 0) {
            return found.get(0);
        }

        // Deploy wallet
        String address = integration.deployWallet(key);

        // Subscribe to events for this wallet
        eventRegister.registerAddedOwnerEvent(address);
        eventRegister.registerRemovedOwnerEvent(address);
        
        // Save wallet (if not existing yet)
        Optional<Wallet> wallet = repository.findById(address);
        if(!wallet.isPresent()) {
            return repository.save(new Wallet(address, 0, Arrays.asList()));
        } else {
            return wallet.get();
        }
    }

    @Override
    public String prepareExec(String address, String to, String data) {
        // TODO should track the call in the DB
        return integration.prepapreExec(address, to, data);
    }

    @Override
    public String exec(String address, String to, String data, String signature) {
        // TODO should track the call in the DB
        return integration.exec(address, to, data, signature);
    }


}
