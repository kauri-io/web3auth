/**
 * 
 */
package net.consensys.web3auth.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

@Configuration
@EnableMongoRepositories("net.consensys.web3auth.module")
@ConditionalOnProperty(name = "web3auth.otp.type", havingValue = "DB")
public class SpringMongoConfig extends AbstractMongoConfiguration {
    
    @Value("${spring.profiles.active:default}")
    private String profileActive;

    @Value("${web3auth.mongodb.host:localhost}")
    private String mongoHost;

    @Value("${web3auth.mongodb.port:27017}")
    private String mongoPort;

    @Value("${web3auth.mongodb.database:web3auth}")
    private String mongoDB;
    
    @Override
    @Bean
    public Mongo mongo() throws Exception {
        return new MongoClient(mongoHost + ":" + mongoPort);
    }
    
    @Override
    protected String getDatabaseName() {
        return mongoDB;
    }
}