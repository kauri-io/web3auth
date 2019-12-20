package net.consensys.web3auth.service.wallet.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends  MongoRepository<Wallet, String>, CustomWalletRepository {

}
