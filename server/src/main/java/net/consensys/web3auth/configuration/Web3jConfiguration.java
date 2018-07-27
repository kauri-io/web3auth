package net.consensys.web3auth.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import net.consensys.web3auth.module.authority.Authority;
import net.consensys.web3auth.module.authority.AuthorityNullService;
import net.consensys.web3auth.module.authority.AuthoritySmartContract;

@Configuration
public class Web3jConfiguration {
    
    @Bean
    @ConditionalOnExpression("${ethereum.enable}")
    Web3j web3j(@Value("${ethereum.node.url}") String url) {
        return Web3j.build(new HttpService(url));
    }

    @Bean
    @ConditionalOnExpression("${ethereum.enable}")
    Authority authoritySmartContract(Web3j web3j) {
        return new AuthoritySmartContract(web3j);
    }
    
    @Bean
    @ConditionalOnExpression("!${ethereum.enable}")
    Authority authorityNullService() {
        return new AuthorityNullService();
    }
    
    
    
}
