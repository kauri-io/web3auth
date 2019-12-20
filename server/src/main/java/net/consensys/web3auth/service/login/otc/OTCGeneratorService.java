package net.consensys.web3auth.service.login.otc;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.consensys.web3auth.common.dto.OTC;

/**
 * Generate and store a unique OTC (One Time Sentence) that will be signed by the client
 * 
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public interface OTCGeneratorService {
    
    /**
     * Generate a unique OTC and store it
     * @return sentence
     */
    OTC generateOTC(String appId);
    
    /**
     * Retrieve a unique OTC from the storage
     * @param id
     * @return sentence
     */
    Optional<OTC> getOTC(String id);
    
    /**
     * Disable a OTC after use
     * @param id
     */
    void disableOTC(String id);
    
    /**
     * Disabled expired OTC
     */
    void scheduleAutoDisablingExpiredOTC();

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
