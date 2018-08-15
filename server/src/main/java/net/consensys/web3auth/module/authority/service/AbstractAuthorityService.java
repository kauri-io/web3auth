/**
 * 
 */
package net.consensys.web3auth.module.authority.service;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.web3j.protocol.Web3j;
import org.web3j.tx.ClientTransactionManager;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.smartcontract.generated.Web3AuthPolicyI;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
@Slf4j
public abstract class AbstractAuthorityService {

    private static final String FROM_ADDRESS = "0x87c5e802ecc299b85946f61cd3dc16b5fda929b8"; // Random ...
    private static final BigInteger GAS_PRICE = new BigInteger("0");
    private static final BigInteger GAS_LIMIT = new BigInteger("0");
    
    protected final Web3j web3j;
    protected final String contractAddress;
    
    public AbstractAuthorityService(Web3j web3j, String contractAddress) {
        this.web3j = web3j;
        this.contractAddress = contractAddress;
    }
    
    protected Web3AuthPolicyI loadContract() {
        log.trace("Loading contract {}", contractAddress);
        return Web3AuthPolicyI.load(contractAddress, web3j, new ClientTransactionManager(web3j, FROM_ADDRESS), GAS_PRICE, GAS_LIMIT);
    }
    
    protected static List<String> bytes32ListToStringList(List<byte[]> bytes32List) {
        return bytes32List.stream().map(AbstractAuthorityService::bytes32ToString).collect(Collectors.toList());
    }
    
    protected static String bytes32ToString(byte[] bytes32) {
        return new String(trim(bytes32));
    }

    protected static byte[] stringToWeb3jSupportedBytes(String theString) {
        final byte[] byteValue = theString.getBytes();
        byte[] byteValueLen32 = new byte[32];
        System.arraycopy(byteValue, 0, byteValueLen32, 0, byteValue.length);

        return byteValueLen32;
    }

    protected static byte[] trim(byte[] bytes) {
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0) {
            --i;
        }

        return Arrays.copyOf(bytes, i + 1);
    }
}
