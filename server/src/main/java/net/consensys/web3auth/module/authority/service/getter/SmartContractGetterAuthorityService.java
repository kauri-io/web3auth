/**
 * 
 */
package net.consensys.web3auth.module.authority.service.getter;

import java.math.BigInteger;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import net.consensys.web3auth.common.dto.Organisation;
import net.consensys.web3auth.module.authority.exception.SmartContractException;
import net.consensys.web3auth.module.authority.service.AbstractSmartContractAuthorityService;
import net.consensys.web3auth.module.authority.service.AuthorityService;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
@Service
@ConditionalOnProperty(name = "web3auth.authority.mode", havingValue = "GETTER", matchIfMissing=false)
public class SmartContractGetterAuthorityService extends AbstractSmartContractAuthorityService implements AuthorityService {
    
    @Autowired
    public SmartContractGetterAuthorityService(
            @Value("${web3auth.authority.ethereum}") String ethereumNode,
            @Value("${web3auth.authority.smartContract}") String smartContract) {
        super(ethereumNode, smartContract);
    }

    @Override
    public Set<Organisation> getOrganisation(String address) {
        
        try {
            @SuppressWarnings("unchecked")
            Set<byte[]> result = (Set<byte[]>) loadContract().getOrganisations(address).send();

            return bytes32ListToStringSet(result).stream()
                    .map(o -> new Organisation(o, getPrivileges(address, o)))
                    .collect(Collectors.toSet());

        } catch (Exception e) {
            throw new SmartContractException(e);
        }
    }

    public int getPrivileges(String user, String organisation) {
        
        try {
            BigInteger result = loadContract().getPrivilege(user, stringToWeb3jSupportedBytes(organisation)).send();

            return result.intValue();
            
        } catch (Exception e) {
            throw new SmartContractException(e);
        }
    }

}
