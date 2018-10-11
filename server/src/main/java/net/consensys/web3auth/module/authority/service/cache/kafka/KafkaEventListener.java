/**
 * 
 */
package net.consensys.web3auth.module.authority.service.cache.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.module.authority.service.cache.CacheProcessor;
import net.consensys.web3auth.module.authority.service.cache.kafka.event.ContractEventMessage;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
@Component
@ConditionalOnProperty(name = "web3auth.authority.mode", havingValue = "CACHE")
@Slf4j
public class KafkaEventListener {
    
    private final CacheProcessor procesor;
    
    @Autowired
    public KafkaEventListener(CacheProcessor procesor) {
        this.procesor = procesor;
    }
    
    @KafkaListener(topics = "#{kafkaSettings.topic}", groupId = "#{kafkaSettings.groupId}")
    public void listenWithHeaders(ContractEventMessage message) {
          log.debug("Receive message {}", message);
          procesor.onEvent(message.getDetails());
    }
}


