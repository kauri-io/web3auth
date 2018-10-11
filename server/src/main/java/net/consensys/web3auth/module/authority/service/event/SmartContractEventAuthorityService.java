package net.consensys.web3auth.module.authority.service.event;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.common.dto.Organisation;
import net.consensys.web3auth.module.authority.exception.SmartContractException;
import net.consensys.web3auth.module.authority.generated.Web3AuthPolicyI;
import net.consensys.web3auth.module.authority.service.AbstractSmartContractAuthorityService;
import net.consensys.web3auth.module.authority.service.AuthorityService;
import rx.Subscription;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "web3auth.authority.mode", havingValue = "EVENT", matchIfMissing=false)
public class SmartContractEventAuthorityService extends AbstractSmartContractAuthorityService implements AuthorityService {

    private static final String EVENT_MEMBER_ENABLED_HASH = EventEncoder.encode(Web3AuthPolicyI.MEMBERENABLED_EVENT);
    private static final String EVENT_MEMBER_DISABLED_HASH = EventEncoder.encode(Web3AuthPolicyI.MEMBERDISABLED_EVENT);

    @Autowired
    public SmartContractEventAuthorityService(
            @Value("${web3auth.authority.ethereum}") String ethereumNode,
            @Value("${web3auth.authority.smartContract}") String smartContract) {
        super(ethereumNode, smartContract);
    }

    @Override
    public Set<Organisation> getOrganisation(String address) {
        log.debug("getOrganisation(address: {})", address);
        
        Set<Organisation> orgs = new HashSet<>();

        try {
            EthFilter filter = new EthFilter(
                    DefaultBlockParameterName.EARLIEST, 
                    DefaultBlockParameterName.LATEST, 
                    contractAddress)
                    .addSingleTopic("0xcb35beb8e9c01046d17a0a7667234208176950a0366eb53af72873f1c78c8b8c");

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
                .subscribe(event -> {
                        String eventHash = event.getTopics().get(0);
                        Organisation org = convert(event);
 
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
