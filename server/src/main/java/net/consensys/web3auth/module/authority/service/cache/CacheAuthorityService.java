/**
 * 
 */
package net.consensys.web3auth.module.authority.service.cache;

import static net.consensys.web3auth.module.authority.service.AbstractSmartContractAuthorityService.remove0x;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.common.dto.Organisation;
import net.consensys.web3auth.module.authority.service.AuthorityService;
import net.consensys.web3auth.module.authority.service.cache.db.UserDomain;
import net.consensys.web3auth.module.authority.service.cache.db.UserRepository;
import net.consensys.web3auth.module.authority.service.cache.kafka.event.ContractEventDetails;
import net.consensys.web3auth.smartcontract.generated.Web3AuthPolicyI;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
@Service
@ConditionalOnProperty(name = "web3auth.authority.mode", havingValue = "CACHE")
@Slf4j
public class CacheAuthorityService  implements CacheProcessor, AuthorityService{

    private UserRepository repository;
    
    @Autowired
    public CacheAuthorityService(UserRepository repository) {
        this.repository = repository;
    }
    
    @Override
    public void onEvent(ContractEventDetails contractEvent) {
        
        // Filter by event name (MemberEnabled or MemberDisabled)
        if(contractEvent.getName().equals(Web3AuthPolicyI.MEMBERADDED_EVENT.getName())
                || contractEvent.getName().equals(Web3AuthPolicyI.MEMBERREMOVED_EVENT.getName())) {
            
            String account = remove0x(contractEvent.getIndexedParameters().get(0).getValueString()).toLowerCase();
            String organisation = contractEvent.getIndexedParameters().get(1).getValueString();
            int role = Integer.parseInt(contractEvent.getIndexedParameters().get(2).getValueString());
            
            Organisation org =  new Organisation(organisation, role);
            
            UserDomain user;
            if(repository.existsById(account)) {
                user = repository.findById(account).get();
                if(!user.getOrgs().add(org)) {
                    user.getOrgs().remove(org);
                    user.getOrgs().add(org);
                }
                
            } else {
                user = new UserDomain(account, org);
            }
            
            log.trace("Saving {} ....", user);
            repository.save(user);             
        }
    }
    
    @Override
    public Set<Organisation> getOrganisation(String address) {
        Optional<UserDomain> userDomain = repository.findById(remove0x(address.toLowerCase()));
        if(!userDomain.isPresent()) {
            return Collections.emptySet();
        }

        return userDomain.get().getOrgs();
    }
}
