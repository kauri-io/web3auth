package net.consensys.web3auth.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.crypto.Sign.SignatureData;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.utils.Numeric;

import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Bytes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.exception.WalletCreationtException;

@Slf4j
public abstract class CryptoUtils {
    
    private static final String PERSONAL_MESSAGE_PREFIX = "\u0019Ethereum Signed Message:\n";

    private CryptoUtils() {}
    
    public static String sign(String message, ECKeyPair key, boolean prefix) {
        log.debug("sign(message={}, key={}, prefix={})", message, "****", prefix);

        // Build message (prefix, sha3)
        byte[] msgHash = buildMessageHash(message, prefix);
        log.debug("msgHash="+Numeric.toHexString(msgHash));
        
        // Sign
        Sign.SignatureData signature = Sign.signMessage(msgHash, key, false);

        // Convert to RLP
        byte[] signatureRLP = Bytes.concat(signature.getR(), signature.getS(), signature.getV());
        
        return Numeric.toHexString(signatureRLP);
    }
    
    public static byte[] hash(String message) {
        
        String pref = PERSONAL_MESSAGE_PREFIX + message.length();

        byte[] msgHash = Hash.sha3((pref+message).getBytes());
        log.trace("msgHash={}", Numeric.toHexString(msgHash));
        
        return msgHash;
    }

    private static byte[] buildMessageHash(String message, boolean prefix) {
        
        // message to bytes 
        byte[] messageByteArray;
        if(message.startsWith("0x")) {
            message = message.substring(2,  message.length()).toUpperCase();
            messageByteArray = BaseEncoding.base16().decode(message);
        } else {
            messageByteArray = message.getBytes();
        }

        // Prefix
        if(prefix) {
            messageByteArray = prefixMessage(messageByteArray);
        }
        
        log.debug("message={}", new String(messageByteArray));

        // Hash message
        return Hash.sha3(messageByteArray);
    }
    
    private static byte[] prefixMessage(byte[] message) {
        try {
            // Prefix
            String pref = PERSONAL_MESSAGE_PREFIX + message.length;

            // Concat prefix and message
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
            outputStream.write(pref.getBytes());
            outputStream.write(message);

            return outputStream.toByteArray();
            
        } catch (IOException e) {
            throw new RuntimeException("Error when generating signature", e);
        }
    }
    
    public static Map<Integer, String> ecrecover(String signature, String message) {
        log.debug("checkSig(signature={}, message={})", signature, message);
    
         byte[] msgHash = hash(message);

         // Signature
         byte[] array = Numeric.hexStringToByteArray(signature);
         byte v = array[64];
         if (v < 27) { v += 27; }
            
         SignatureData sd = new SignatureData(v, (byte[]) Arrays.copyOfRange(array, 0, 32), (byte[])  Arrays.copyOfRange(array, 32, 64));
         
         Map<Integer, String> addresses = new HashMap<>();
         
         // Iterate for each possible key to recover
         for(int i=0; i<4; i++) {
             BigInteger publicKey = Sign.recoverFromSignature((byte)i, new ECDSASignature(new BigInteger(1, sd.getR()), new BigInteger(1, sd.getS())), msgHash);
             log.trace("publicKey ({}) = {}", i, publicKey);
                
             if(publicKey != null) {
                 addresses.put(i, "0x" + Keys.getAddress(publicKey));
             }
         }
         
         log.debug("checkSig(signature={}, message={}) => addresses recovered {}", signature, message, addresses);
 
         return addresses;
    }
    
    public static PrivateKeyAddress createNewWallet() {
        
        try {
            ECKeyPair ecKeyPair = Keys.createEcKeyPair();
            BigInteger privateKey = ecKeyPair.getPrivateKey();
            WalletFile wallet = Wallet.createLight(UUID.randomUUID().toString(), ecKeyPair);
            
            return new PrivateKeyAddress(privateKey.toString(16), wallet.getAddress());
            
        } catch(Exception ex) {
            throw new WalletCreationtException("Error while creating new wallet", ex);
        }
    }
    
    @Getter @Setter
    @AllArgsConstructor
    public static class PrivateKeyAddress {
        private String privateKey;
        private String address;
    }
    
}
