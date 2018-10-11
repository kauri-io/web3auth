package net.consensys.web3auth.module.login.service.ots;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.consensys.web3auth.module.login.model.OTS;

/**
 * Generate and store a unique OTS (One Time Sentence) that will be signed by the client
 * 
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public interface OTSGeneratorService {
    
    /**
     * Generate a unique OTS and store it
     * @return sentence
     */
    OTS generateOTS(String appId);
    
    /**
     * Retrieve a unique OTS from the storage
     * @param id
     * @return sentence
     */
    Optional<OTS> getOTS(String id);
    
    /**
     * Disable a OTS after use
     * @param id
     */
    void disableOTS(String id);
    
    /**
     * Disabled expired OTS
     */
    void scheduleAutoDisablingExpiredOTS();

    /**
     * Generate a random sentence
     * @param prefix Prefix
     * @param len    Length of the sentence
     * @param dic    Dictionary
     * @return       Random sentence
     */
    static String generate(String prefix, int len, String dic) {
        SecureRandom random = new SecureRandom();
        
        return prefix + IntStream.range(0, len)
            .mapToObj(i ->  dic.charAt(random.nextInt(dic.length())) )
            .map(String::valueOf)
            .collect(Collectors.joining());
    }
}
