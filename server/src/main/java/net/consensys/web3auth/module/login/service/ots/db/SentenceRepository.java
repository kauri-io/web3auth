package net.consensys.web3auth.module.login.service.ots.db;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import net.consensys.web3auth.module.login.model.LoginSentence;


@Repository
public interface SentenceRepository extends MongoRepository<LoginSentence, String> {

}
