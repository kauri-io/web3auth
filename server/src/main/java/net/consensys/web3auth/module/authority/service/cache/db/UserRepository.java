package net.consensys.web3auth.module.authority.service.cache.db;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "web3auth.authority.mode", havingValue = "CACHE")
public interface UserRepository extends MongoRepository<UserDomain, String> {

}
