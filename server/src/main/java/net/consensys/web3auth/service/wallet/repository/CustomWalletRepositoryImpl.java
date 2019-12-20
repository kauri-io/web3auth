package net.consensys.web3auth.service.wallet.repository;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.Assert;

public class CustomWalletRepositoryImpl implements CustomWalletRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public CustomWalletRepositoryImpl(MongoTemplate mongoTemplate) {
      Assert.notNull(mongoTemplate, "MongoTemplate must not be null!");
      this.mongoTemplate = mongoTemplate;
    }
    
    @Override
    public List<Wallet> findByKey(String key) {
        Query query = new Query();      
        query.addCriteria(
                Criteria.where("keys").regex(Pattern.compile(key, Pattern.CASE_INSENSITIVE)));
        
        return mongoTemplate.find(query, Wallet.class, "wallet");
    }

}
