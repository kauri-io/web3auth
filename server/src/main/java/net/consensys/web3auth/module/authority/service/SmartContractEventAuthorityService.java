/**
 * 
 */
package net.consensys.web3auth.module.authority.service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.common.dto.Organisation;
import net.consensys.web3auth.module.authority.exception.SmartContractException;
import rx.Subscription;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
@Slf4j
public class SmartContractEventAuthorityService extends AbstractAuthorityService implements AuthorityService {


    @Autowired
    public SmartContractEventAuthorityService(Web3j web3j, String contractAddress) {
        super(web3j, contractAddress);
    }
    
    @Override
    public List<Organisation> getOrganisation(String address) {


        try {
            loadContract().memberEnabledEventObservable(
                    DefaultBlockParameter.valueOf(new BigInteger("0")), 
                    DefaultBlockParameter.valueOf(web3j.ethBlockNumber().send().getBlockNumber()));
            
            return null;
            
        } catch (IOException e) {
            throw new SmartContractException(e);
        }
        
    }

}
