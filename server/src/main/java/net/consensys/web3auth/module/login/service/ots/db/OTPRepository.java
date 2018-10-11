package net.consensys.web3auth.module.login.service.ots.db;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import net.consensys.web3auth.module.login.model.OTS;



@Repository
@ConditionalOnProperty(name = "web3auth.otp.type", havingValue = "DB")
public interface OTPRepository extends MongoRepository<OTS, String> {

}
