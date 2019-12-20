package net.consensys.web3auth.service.socialconnect.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialConnectAccountRepository extends  MongoRepository<SocialConnectAccount, String> {
 
    Optional<SocialConnectAccount> findOneByEmail(String email);
    
}
