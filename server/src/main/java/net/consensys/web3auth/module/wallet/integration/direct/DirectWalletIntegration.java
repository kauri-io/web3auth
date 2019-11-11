package net.consensys.web3auth.module.wallet.integration.direct;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.websocket.WebSocketService;
import org.web3j.tx.ChainId;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.response.NoOpProcessor;
import org.web3j.utils.Numeric;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.module.wallet.exception.ConnectionException;
import net.consensys.web3auth.module.wallet.exception.ContractException;
import net.consensys.web3auth.module.wallet.integration.WalletIntegration;
import net.consensys.web3auth.module.wallet.model.Key.KeyRole;
import net.consensys.web3auth.smartcontract.generated.Wallet;

@Slf4j
@ConditionalOnProperty(name = "web3auth.wallet.mode", havingValue = "DIRECT")
public class DirectWalletIntegration implements WalletIntegration {

    private final Web3j web3j;
    private final TransactionManager transactionManager;
    private final  ContractGasProvider contractGasProvider;
    
    public DirectWalletIntegration(
            @Value("${web3auth.wallet.direct.mnemonic") String mnemonic,
            @Value("${web3auth.wallet.direct.rpcUrl") String rpcUrl) {
        this.web3j = connect(rpcUrl);

        Credentials credentials = WalletUtils.loadBip39Credentials("", mnemonic);
        log.debug("Account loaded from mnemonic: {}", credentials.getAddress());
        this.transactionManager = new RawTransactionManager(web3j, credentials, ChainId.NONE, new NoOpProcessor(web3j));
        
        this.contractGasProvider = new DefaultGasProvider();
    }
    
    @Override
    public String deployWallet(String key, String hash, String signature) {
        
        try {
            Wallet wallet = Wallet.deploy(
                    web3j, 
                    transactionManager, 
                    contractGasProvider, 
                    key, 
                    Numeric.hexStringToByteArray(hash), 
                    Numeric.hexStringToByteArray(signature)).send();
            
            return wallet.getContractAddress();
            
        } catch (Exception ex) {
            throw new ContractException("Error while deploying contract", ex);
        }
    }

    @Override
    public void addKey(String wallet, String key, KeyRole role, String signature) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeKey(String wallet, String key, String signature) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Integer getNonce(String wallet) {
        // TODO Auto-generated method stub
        return null;
    }
    

    /**
     * 
     * @param url
     * @return
     */
    private static Web3j connect(String url) {
        //ValidatorUtils.requireNonEmpty(url, "url");
        
        final URI uri = parseURI(url);
        Web3jService service;

        if (uri.getScheme().startsWith("ws")) {
            try {
                WebSocketService wsService = new WebSocketService(url, false);
                wsService.connect();
                service = wsService;
            } catch (ConnectException ex) {
                throw new ConnectionException("Unable to connect to eth node websocket", ex);
            }
        } else {
            service = new HttpService(url);
        }
        
        final Web3j web3j = Web3j.build(service);
        
        if(log.isDebugEnabled()) {
            try {
                String clientVersion = web3j.web3ClientVersion().send().getWeb3ClientVersion();
                log.debug("Connected to Ethereum node {} : {}", url, clientVersion);
            } catch (IOException ex) {
                throw new ConnectionException("Unable to load client version", ex);
            }
        }
        
        return web3j;
    }

    /**
     * 
     * @param serverUrl
     * @return
     */
    public static URI parseURI(String serverUrl) {
        try {
            return new URI(serverUrl);
        } catch (URISyntaxException e) {
            throw new RuntimeException(String.format("Failed to parse URL: '%s'", serverUrl), e);
        }
    }
    
}
