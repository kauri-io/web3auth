package net.consensys.web3auth.configuration;

import java.net.ConnectException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.websocket.WebSocketService;

@Configuration
public class Web3jConfiguration {
    
    @Bean
    @ConditionalOnExpression("${ethereum.enable:true}")
    Web3j web3j(@Value("${ethereum.node.url}") String url, @Value("${ethereum.node.protocol}") String protocol) throws ConnectException {
        
        if(protocol.equals("WEB_SOCKET")) {
            WebSocketService web3jService = new WebSocketService(url, false);
            web3jService.connect();
            return Web3j.build(web3jService);
            
        } else {
            return Web3j.build(new HttpService(url));
        }
    } 
}
