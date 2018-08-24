/**
 * 
 */
package net.consensys.web3auth.module.authority.service;

import java.math.BigInteger;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.protocol.Web3j;

import net.consensys.web3auth.common.dto.Organisation;
import net.consensys.web3auth.module.authority.exception.SmartContractException;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public class SmartContractGetterAuthorityService extends AbstractAuthorityService implements AuthorityService {

    @Autowired
    public SmartContractGetterAuthorityService(Web3j web3j, String contractAddress) {
        super(web3j, contractAddress);
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
