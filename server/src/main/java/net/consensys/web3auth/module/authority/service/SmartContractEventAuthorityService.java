package net.consensys.web3auth.module.authority.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.common.dto.Organisation;
import net.consensys.web3auth.module.authority.exception.SmartContractException;
import net.consensys.web3auth.module.authority.service.generated.Web3AuthPolicyI;
import rx.Subscription;
import rx.functions.Action1;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
@Slf4j
public class SmartContractEventAuthorityService extends AbstractAuthorityService implements AuthorityService {

    private static final String EVENT_MEMBER_ENABLED_HASH = EventEncoder.encode(Web3AuthPolicyI.MEMBERENABLED_EVENT);
    private static final String EVENT_MEMBER_DISABLED_HASH = EventEncoder.encode(Web3AuthPolicyI.MEMBERDISABLED_EVENT);

    @Autowired
    public SmartContractEventAuthorityService(Web3j web3j, String contractAddress) {
        super(web3j, contractAddress);
    }

    @Override
    public Set<Organisation> getOrganisation(String address) {
        log.debug("getOrganisation(address: {})", address);
        
        Set<Organisation> orgs = new HashSet<>();

        try {
            EthFilter filter = new EthFilter(
                    DefaultBlockParameterName.EARLIEST, 
                    DefaultBlockParameterName.LATEST, 
                    contractAddress);

            Subscription subscription = web3j.ethLogObservable(filter)
                .filter(e-> {
                    // Filter by events (only MemberEnabled and MemberDisabled)
                    if(!e.getTopics().get(0).equals(EVENT_MEMBER_ENABLED_HASH) && !e.getTopics().get(0).equals(EVENT_MEMBER_DISABLED_HASH)) {
                        return false;
                    }
                    // Filter by user address
                    Address a = (Address) FunctionReturnDecoder.decodeIndexedValue(e.getTopics().get(1), new TypeReference<Address>() {});
                    return remove0x(address.toLowerCase()).equals(remove0x(a.getValue().toLowerCase())) ;
                })
                .subscribe(new Action1<Log>() {
                    @Override    
                    public void call(Log e) {
                        String eventHash = e.getTopics().get(0);
                        Organisation org = convert(e);
 
                        if(eventHash.equals(EVENT_MEMBER_ENABLED_HASH)) {
                            log.debug("memberEnabled for {}", org);
                            if(orgs.contains(org)) {
                                orgs.remove(org);
                            }
                            orgs.add(org);
                            
                        } else if(eventHash.equals(EVENT_MEMBER_DISABLED_HASH)) {
                            log.debug("memberDisabled for {}", org);
                            orgs.remove(org);
                        }
                    }
                });
            
            subscription.unsubscribe();

            log.debug("getOrganisation(address: {}): {}", address, orgs);
            return orgs;
            
        } catch (Exception e) {
            throw new SmartContractException(e);
        }
        
    }
    
    private static Organisation convert(Log event) {
        Bytes32 org = (Bytes32) FunctionReturnDecoder.decodeIndexedValue(event.getTopics().get(2), new TypeReference<Bytes32>() {});
        Uint8 role = (Uint8) FunctionReturnDecoder.decodeIndexedValue(event.getTopics().get(3), new TypeReference<Uint8>() {});

        return new Organisation(bytes32ToString(org.getValue()), role.getValue().intValue());
    }

}
