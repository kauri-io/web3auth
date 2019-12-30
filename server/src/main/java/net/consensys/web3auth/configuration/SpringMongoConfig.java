/**
 * 
 */
package net.consensys.web3auth.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.util.StringUtils;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

@Configuration
public class SpringMongoConfig extends AbstractMongoConfiguration {
    
    @Value("${spring.data.mongodb.host:localhost}")
    private String mongoHost;

    @Value("${spring.data.mongodb.port:27017}")
    private String mongoPort;

    @Value("${spring.data.mongodb.uri:#{null}}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database:web3auth}")
    private String mongoDB;
    
    @Override
    @Bean
    public MongoClient mongoClient() {
        if(StringUtils.isEmpty(mongoUri)) {
            return new MongoClient(mongoHost + ":" + mongoPort);
        } else {
            return new MongoClient(new MongoClientURI(mongoUri));
            
        }
    }
    
    @Override
    protected String getDatabaseName() {
        return mongoDB;
    }
}