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
import net.consensys.web3auth.module.authority.exception.SmartContractException;
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

        try {
            // Filter by event name (MemberAdded or MemberRemoved and memberChanged)
            if(  contractEvent.getName().equals(Web3AuthPolicyI.MEMBERADDED_EVENT.getName())
              || contractEvent.getName().equals(Web3AuthPolicyI.MEMBERREMOVED_EVENT.getName())
              || contractEvent.getName().equals(Web3AuthPolicyI.MEMBERROLECHANGED_EVENT.getName())) {
                
                boolean add = contractEvent.getName().equals(Web3AuthPolicyI.MEMBERADDED_EVENT.getName())
                           || contractEvent.getName().equals(Web3AuthPolicyI.MEMBERROLECHANGED_EVENT.getName());
                
                String account = getAccountFromContractEvent(contractEvent);
                Organisation org =  getOrganisationFromContractEvent(contractEvent);
                
                UserDomain user = repository.findById(account)
                        .map(u -> {
                            if(add && !u.getOrgs().add(org)) { // add or edit the organisation
                                u.getOrgs().remove(org);
                                u.getOrgs().add(org);
                                
                            } else if (!add) { // remove the organisation
                                u.getOrgs().remove(org);
                            }
                            
                            return u;
                        }).orElseGet(() -> new UserDomain(account, org));

                log.debug("Saving {} ....", user);
                repository.save(user);             
            }
            
        } catch(Exception ex) {
            log.error("Error while processing event {}", contractEvent, ex);
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
    
    private String getAccountFromContractEvent(ContractEventDetails contractEvent) {

        if(contractEvent.getName().equals(Web3AuthPolicyI.MEMBERADDED_EVENT.getName())) {
            
            return remove0x(contractEvent.getIndexedParameters().get(0).getValueString()).toLowerCase();
            
        } else if (contractEvent.getName().equals(Web3AuthPolicyI.MEMBERROLECHANGED_EVENT.getName())
                || contractEvent.getName().equals(Web3AuthPolicyI.MEMBERREMOVED_EVENT.getName())) {
            
            return remove0x(contractEvent.getIndexedParameters().get(1).getValueString()).toLowerCase();
            
        } else {
            throw new SmartContractException("Unknown smart contract event: " + contractEvent.getName());
        }
    }
    
    private Organisation getOrganisationFromContractEvent(ContractEventDetails contractEvent) {

        if(contractEvent.getName().equals(Web3AuthPolicyI.MEMBERADDED_EVENT.getName())) {

            String organisation = contractEvent.getIndexedParameters().get(1).getValueString();
            int role = Integer.parseInt(contractEvent.getIndexedParameters().get(2).getValueString());
            
            return  new Organisation(organisation, role);
            
        } else if (contractEvent.getName().equals(Web3AuthPolicyI.MEMBERROLECHANGED_EVENT.getName())
                || contractEvent.getName().equals(Web3AuthPolicyI.MEMBERREMOVED_EVENT.getName())) {

            String organisation = contractEvent.getIndexedParameters().get(0).getValueString();
            int role = Integer.parseInt(contractEvent.getIndexedParameters().get(2).getValueString());
            
            return  new Organisation(organisation, role);
            
        } else {
            throw new SmartContractException("Unknown smart contract event: " + contractEvent.getName());
        }
    }
}
