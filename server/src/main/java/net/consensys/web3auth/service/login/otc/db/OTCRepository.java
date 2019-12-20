package net.consensys.web3auth.service.login.otc.db;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import net.consensys.web3auth.common.dto.OTC;



@Repository
@ConditionalOnProperty(name = "web3auth.otc.type", havingValue = "DB")
public interface OTCRepository extends MongoRepository<OTC, String> {

}
