/**
 * 
 */
package net.consensys.web3auth.module.authority.service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.websocket.WebSocketService;
import org.web3j.tx.ClientTransactionManager;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.smartcontract.generated.Web3AuthPolicyI;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
@Slf4j
public abstract class AbstractSmartContractAuthorityService {

    private static final String FROM_ADDRESS = "0x87c5e802ecc299b85946f61cd3dc16b5fda929b8"; // Random ...
    private static final BigInteger GAS_PRICE = new BigInteger("0");
    private static final BigInteger GAS_LIMIT = new BigInteger("0");
    
    protected final Web3j web3j;
    protected final String contractAddress;
    
    public AbstractSmartContractAuthorityService(String ethereumUrl, String contractAddress) {
        try {
            this.web3j = connect(ethereumUrl);
            this.contractAddress = contractAddress;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    protected Web3j connect(String url) throws IOException {
        Objects.requireNonNull(url, "ethereum url cannot be null");
        
        log.debug("Connecting to Ethereum node {}...",url);
        Web3j web3j;
        
        //////// WEBSOCKET ///////////////////////////////////
        if(url.startsWith("ws")) { 
            log.debug("WebSocket mode");
            WebSocketService web3jService = new WebSocketService(url, true);
            web3jService.connect();
            web3j = Web3j.build(web3jService);
            
        //////// HTTP ///////////////////////////////////
        } else { 
            log.debug("HTTP mode");
            web3j = Web3j.build(new HttpService(url));
        }

        if(log.isDebugEnabled()) {
            String clientVersion = web3j.web3ClientVersion().send().getWeb3ClientVersion();
            log.debug("Connected to Ethereum node {} : {}", url, clientVersion);
        }
        
        return web3j;
    } 
    
    protected Web3AuthPolicyI loadContract() {
        log.trace("Loading contract {}", contractAddress);
        return Web3AuthPolicyI.load(contractAddress, web3j, new ClientTransactionManager(web3j, FROM_ADDRESS), GAS_PRICE, GAS_LIMIT);
    }
    
    protected static List<String> bytes32ListToStringList(List<byte[]> bytes32List) {
        return bytes32List.stream().map(AbstractSmartContractAuthorityService::bytes32ToString).collect(Collectors.toList());
    }
    
    protected static Set<String> bytes32ListToStringSet(Set<byte[]> result) {
        return result.stream().map(AbstractSmartContractAuthorityService::bytes32ToString).collect(Collectors.toSet());
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
    

    
    public static String remove0x(String str) {
        if(!str.startsWith("0x")) {
            return str;
        }
        
        return str.substring(2);
    }
}
