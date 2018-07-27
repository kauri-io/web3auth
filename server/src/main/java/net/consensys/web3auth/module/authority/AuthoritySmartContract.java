package net.consensys.web3auth.module.authority;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.tx.ClientTransactionManager;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.smartcontract.generated.AuthorityI;

@Slf4j
public class AuthoritySmartContract implements Authority {

    private static final String FROM_ADDRESS = "0x87c5e802ecc299b85946f61cd3dc16b5fda929b8"; // Random ...
    private static final BigInteger GAS_PRICE = new BigInteger("0");
    private static final BigInteger GAS_LIMIT = new BigInteger("0");

    private Web3j web3j;

    @Autowired
    public AuthoritySmartContract(Web3j web3j) {
        this.web3j = web3j;
        
        try {
            Web3ClientVersion web3ClientVersion = web3j.web3ClientVersion().send();
            log.trace(web3ClientVersion.getWeb3ClientVersion());
            
        } catch (IOException e) {
            throw new SmartContractException(e);
        }
    }

    private AuthorityI loadContract(String contractAddress) {
        log.trace("Loading contract {}", contractAddress);
        return AuthorityI.load(contractAddress, web3j, new ClientTransactionManager(web3j, FROM_ADDRESS), GAS_PRICE, GAS_LIMIT);
    }

    public List<String> getOrganisations(String contractAddress, String user) {
        
        try {
            AuthorityI authorityContract = loadContract(contractAddress);

            @SuppressWarnings("unchecked")
            List<byte[]> result = (List<byte[]>) authorityContract.getOrganisations(user).send();

            return bytes32ListToStringList(result);

        } catch (Exception e) {
            throw new SmartContractException(e);
        }
    }

    public List<String> getPrivileges(String contractAddress, String user, String organisation) {
        
        try {
            AuthorityI authorityContract = loadContract(contractAddress);

            @SuppressWarnings("unchecked")
            List<byte[]> result = (List<byte[]>) authorityContract
                    .getPrivileges(user, stringToWeb3jSupportedBytes(organisation)).send();

            return bytes32ListToStringList(result);
            
        } catch (Exception e) {
            throw new SmartContractException(e);
        }
    }

    private static List<String> bytes32ListToStringList(List<byte[]> bytes32List) {
        return bytes32List.stream().map(val -> new String(trim(val))).collect(Collectors.toList());
    }

    private static byte[] stringToWeb3jSupportedBytes(String theString) {
        final byte[] byteValue = theString.getBytes();
        byte[] byteValueLen32 = new byte[32];
        System.arraycopy(byteValue, 0, byteValueLen32, 0, byteValue.length);

        return byteValueLen32;
    }

    private static byte[] trim(byte[] bytes) {
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0) {
            --i;
        }

        return Arrays.copyOf(bytes, i + 1);
    }

}
