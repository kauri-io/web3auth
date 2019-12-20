package net.consensys.web3auth.service.socialconnect.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialConnectCSRFRepository extends  MongoRepository<SocialConnectCSRF, String> {
     
}
