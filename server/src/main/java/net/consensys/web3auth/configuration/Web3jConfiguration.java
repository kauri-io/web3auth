package net.consensys.web3auth.configuration;

import java.io.IOException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.websocket.WebSocketService;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class Web3jConfiguration {
    
    @Bean
    @ConditionalOnExpression("${ethereum.enable:true}")
    Web3j web3j(@Value("${ethereum.node.url}") String url) throws IOException {
        Objects.requireNonNull(url, "ethereum.node.url cannot be null");
        
        log.debug("Connecting to Ethereum node {}...",url);
        Web3j web3j;
        
        //////// WEBSOCKET ///////////////////////////////////
        if(url.startsWith("ws")) { 
            log.debug("WebSocket mode");
            WebSocketService web3jService = new WebSocketService(url, true);
            web3jService.connect();
            web3j = Web3j.build(web3jService);
            
        //////// HTTP ///////////////////////////////////
        } else { 
            log.debug("HTTP mode");
            web3j = Web3j.build(new HttpService(url));
        }

        if(log.isDebugEnabled()) {
            String clientVersion = web3j.web3ClientVersion().send().getWeb3ClientVersion();
            log.debug("Connected to Ethereum node {} : {}", url, clientVersion);
        }
        
        return web3j;
    } 
}
