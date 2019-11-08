package net.consensys.web3auth.module.wallet.repository;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import net.consensys.web3auth.module.wallet.model.Wallet;

@Repository
@ConditionalOnProperty(name = "web3auth.wallet.enable", havingValue = "true")
public interface WalletRepository extends  MongoRepository<Wallet, String> {

    @Query(value = "{ 'keys': { $elemMatch: { 'key' : ?0 } }}")
    List<Wallet> findByKey(String key);
}
