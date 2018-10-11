/**
 * 
 */
package net.consensys.web3auth.module.authority.service.cache;

import net.consensys.web3auth.module.authority.service.cache.kafka.event.ContractEventDetails;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public interface CacheProcessor {

    void onEvent(ContractEventDetails contractEvent);
    
}
