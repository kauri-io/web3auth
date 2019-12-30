package net.consensys.web3auth.service.wallet.integration;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.websocket.WebSocketService;
import org.web3j.tx.ChainId;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.tx.response.NoOpProcessor;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;
import org.web3j.tx.response.TransactionReceiptProcessor;
import org.web3j.utils.Numeric;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.configuration.Web3AuthSettings.WalletSetting;
import net.consensys.web3auth.exception.ConnectionException;
import net.consensys.web3auth.exception.ContractDeploymentException;
import net.consensys.web3auth.exception.ContractException;
import net.consensys.web3auth.service.admin.ConfigService;
import net.consensys.web3auth.smartcontract.generated.GnosisSafe;
import net.consensys.web3auth.smartcontract.generated.ProxyFactory;
import net.consensys.web3auth.smartcontract.generated.ProxyFactory.ProxyCreationEventResponse;

@Service
@ConditionalOnProperty(name = "web3auth.wallet.mode", havingValue = "DIRECT")
@Slf4j
public class DirectWalletIntegration implements WalletIntegration {

    private static final String ADDRESS_ZERO = Address.DEFAULT.getValue();
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    
    private static final BigInteger REQUIRED_SIGNATURES = BigInteger.ONE;
    private static final BigInteger GAS_LIMIT = BigInteger.valueOf(8_000_000); //TODO change to property
    private static final BigInteger GAS_PRICE = BigInteger.valueOf(22_000_000_000L); //TODO change to property
    
    @AllArgsConstructor
    public enum SAFE_OPERATION { 
            CALL(BigInteger.valueOf(0)), DELEGATE_CALL(BigInteger.valueOf(1)), CREATE(BigInteger.valueOf(2));
        BigInteger code;
     }
    
    private final String relayer;
    private final Web3j web3j;
    private final TransactionManager transactionManager;
    private final ContractGasProvider contractGasProvider;
    private final ProxyFactory proxyFactory;
    private final GnosisSafe gnosisSafeMasterCopy;
    private final TransactionReceiptProcessor receiptProcessor;
    private final  Credentials credentials;
    
    public DirectWalletIntegration(ConfigService configService) {
        WalletSetting setting = configService.getWalletSetting();
        
        this.web3j = connect(setting.getDirect().getRpcUrl());

        this.credentials = readMnemonic(setting.getDirect().getMnemonic(), "");
        log.debug("Account loaded from mnemonic: {}", credentials.getAddress());
        this.relayer = credentials.getAddress();

        this.transactionManager = new RawTransactionManager(web3j, credentials, ChainId.NONE, new NoOpProcessor(web3j));
        this.contractGasProvider = new StaticGasProvider(GAS_PRICE, GAS_LIMIT);
        this.receiptProcessor = new PollingTransactionReceiptProcessor(web3j, 
                TransactionManager.DEFAULT_POLLING_FREQUENCY,
                TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH);
        
        // Load or deploy ProxyContact
        if(StringUtils.isEmpty(setting.getDirect().getProxyFactory())) {
            proxyFactory = deployProxyContract();
        } else {
            proxyFactory = loadProxyContract(setting.getDirect().getProxyFactory());
        }

        // Load or deploy GnosisSafe master copy
        if(StringUtils.isEmpty(setting.getDirect().getGnosisSafeMasterCopy())) {
            gnosisSafeMasterCopy = deployGnosisSafeMasterCopy();
        } else {
            gnosisSafeMasterCopy = loadGnosisSafe(setting.getDirect().getGnosisSafeMasterCopy());
        }
    }
    
    private ProxyFactory loadProxyContract(String address) {
        return ProxyFactory.load(address, web3j, transactionManager, contractGasProvider);
    }
    
    private ProxyFactory deployProxyContract() {
        try {
            ProxyFactory proxyFactory = ProxyFactory.deploy(web3j, credentials, contractGasProvider).send();
            log.info("ProxyFactory contract deployed at the address {}", proxyFactory.getContractAddress());
            return proxyFactory;
            
        } catch(Exception ex) {
            throw new ContractDeploymentException("ProxyFactory", ex);
        }
    }
    
    private GnosisSafe loadGnosisSafe(String address) {
        return GnosisSafe.load(address, web3j, transactionManager, contractGasProvider);
    }
    
    private GnosisSafe deployGnosisSafeMasterCopy() {
        try {
            GnosisSafe gnosisSafe = GnosisSafe.deploy(web3j, credentials, contractGasProvider).send();

            TransactionReceipt receipt = gnosisSafe.setup(
                    Arrays.asList(relayer), 
                    REQUIRED_SIGNATURES, 
                    ADDRESS_ZERO, 
                    EMPTY_BYTE_ARRAY, 
                    ADDRESS_ZERO, 
                    ADDRESS_ZERO, 
                    BigInteger.ZERO, 
                    ADDRESS_ZERO).send();
            
            receipt = this.receiptProcessor.waitForTransactionReceipt(receipt.getTransactionHash());
            
            log.info("GnosisSafe master copy contract deployed at the address {}. Setup transaction: {}", 
                    gnosisSafe.getContractAddress(), receipt.getTransactionHash());
            
            return gnosisSafe;
            
        } catch(Exception ex) {
            throw new ContractDeploymentException("GnosisSafe", ex);
        }
    }
    
    @Override
    public String getProxyAddress() {
        return proxyFactory.getContractAddress();
    }
    
    @Override
    public String deployWallet(String key) {
        
        try {
            Function function = new Function(GnosisSafe.FUNC_SETUP,
                    Arrays.asList(
                            new DynamicArray<Address>(
                                    org.web3j.abi.datatypes.Address.class,
                                    org.web3j.abi.Utils.typeMap(Arrays.asList(key), Address.class)), 
                            new Uint256(REQUIRED_SIGNATURES), 
                            new Address(ADDRESS_ZERO), 
                            new DynamicBytes(EMPTY_BYTE_ARRAY), 
                            new Address(ADDRESS_ZERO), 
                            new Address(ADDRESS_ZERO), 
                            new Uint256(BigInteger.ZERO), 
                            new Address(ADDRESS_ZERO)), 
                    Collections.emptyList());
    
            String txData = FunctionEncoder.encode(function);
    
            TransactionReceipt receipt = proxyFactory.createProxy(gnosisSafeMasterCopy.getContractAddress(), Numeric.hexStringToByteArray(txData)).send();

            receipt = this.receiptProcessor.waitForTransactionReceipt(receipt.getTransactionHash());

            List<ProxyCreationEventResponse> events = proxyFactory.getProxyCreationEvents(receipt);
            
            if(events.isEmpty()) {
                throw new ContractException("Gnosis deployment should have at least one event log");
            }
            
            return events.get(0).proxy;
            
        } catch(Exception ex) {
            throw new ContractDeploymentException("GnosisSafe", ex);
        }
    }

    @Override
    public BigInteger getNonce(String wallet) {
        
        try {
            GnosisSafe safe = loadGnosisSafe(wallet);
            return safe.nonce().send();
        
        } catch(Exception ex) {
            throw new ContractException("GnosisSafe.nonce", ex);
        }
    }



    @Override
    public List<String> getOwners(String wallet) {
        
        try {
            GnosisSafe safe = loadGnosisSafe(wallet);
            
            @SuppressWarnings("unchecked")
            List<String> result = safe.getOwners().send();
            
            return result;
        
        } catch(Exception ex) {
            throw new ContractException("GnosisSafe.getOwners", ex);
        }
    }
    
    @Override
    public String prepapreExec(String wallet, String to, String data) {

        try {
            GnosisSafe safe = loadGnosisSafe(wallet);
            
            byte[] txHash = safe.getTransactionHash(
                    to, 
                    BigInteger.ZERO, 
                    Numeric.hexStringToByteArray(data), 
                    SAFE_OPERATION.CALL.code, 
                    BigInteger.ZERO, 
                    BigInteger.ZERO, 
                    BigInteger.ZERO, 
                    ADDRESS_ZERO, 
                    ADDRESS_ZERO, 
                    getNonce(wallet)).send();
            
            return Numeric.toHexString(txHash);
        
        } catch(Exception ex) {
            throw new ContractException("GnosisSafe.getTransactionHash", ex);
        }
    }

    @Override
    public String exec(String wallet, String to, String data, String signature) {
        
        try {
            GnosisSafe safe = loadGnosisSafe(wallet);
            
            TransactionReceipt receipt = safe.execTransaction(
                    to, 
                    BigInteger.ZERO, 
                    Numeric.hexStringToByteArray(data), 
                    SAFE_OPERATION.CALL.code, 
                    BigInteger.ZERO, 
                    BigInteger.ZERO, 
                    BigInteger.ZERO, 
                    ADDRESS_ZERO, 
                    ADDRESS_ZERO, 
                    Numeric.hexStringToByteArray(signature)).send();

            receipt = this.receiptProcessor.waitForTransactionReceipt(receipt.getTransactionHash());
            
            return receipt.getTransactionHash();
        
        } catch(Exception ex) {
            throw new ContractException("GnosisSafe.exec", ex);
        }
    }
    
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

    public static URI parseURI(String serverUrl) {
        try {
            return new URI(serverUrl);
        } catch (URISyntaxException e) {
            throw new RuntimeException(String.format("Failed to parse URL: '%s'", serverUrl), e);
        }
    }
    
    private static Credentials readMnemonic(String mnemonic, String password) {
        
        //Derivation path wanted: // m/44'/60'/0'/0
        int[] derivationPath = {44 | Bip32ECKeyPair.HARDENED_BIT, 60 | Bip32ECKeyPair.HARDENED_BIT, 0 | Bip32ECKeyPair.HARDENED_BIT, 0,0};

        // Generate a BIP32 master keypair from the mnemonic phrase
        Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(MnemonicUtils.generateSeed(mnemonic, password));

        // Derived the key using the derivation path
        Bip32ECKeyPair  derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, derivationPath);

        // Load the wallet for the derived key
        Credentials credentials = Credentials.create(derivedKeyPair);
        
        return credentials;
    }
    
}
