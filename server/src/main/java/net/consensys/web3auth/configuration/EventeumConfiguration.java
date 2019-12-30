package net.consensys.web3auth.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.consensys.eventeum.integration.broadcast.blockchain.BlockchainEventBroadcaster;
import net.consensys.eventeum.integration.broadcast.blockchain.ListenerInvokingBlockchainEventBroadcaster;
import net.consensys.web3auth.service.wallet.integration.eventeum.BlockchainEventListener;

/**
 * Configures application context for Eventeum specific beans.
 *
 * @author Craig Williams
 */
@Configuration
public class EventeumConfiguration {

    @Bean
    public BlockchainEventBroadcaster listenerBroadcaster(BlockchainEventListener listener) {
        return new ListenerInvokingBlockchainEventBroadcaster(listener);
    }

}
