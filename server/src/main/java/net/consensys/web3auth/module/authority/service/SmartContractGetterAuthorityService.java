/**
 * 
 */
package net.consensys.web3auth.module.authority.service;

import java.util.List;
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
    public List<Organisation> getOrganisation(String address) {
        try {
            @SuppressWarnings("unchecked")
            List<byte[]> result = (List<byte[]>) loadContract().getOrganisations(address).send();

            return bytes32ListToStringList(result).stream()
                    .map(o -> new Organisation(o, getPrivileges(address, o)))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new SmartContractException(e);
        }
    }

    public String getPrivileges(String user, String organisation) {
        
        try {
            byte[] result = (byte[]) loadContract().getPrivilege(user, stringToWeb3jSupportedBytes(organisation)).send();

            return bytes32ToString(result);
            
        } catch (Exception e) {
            throw new SmartContractException(e);
        }
    }

}
