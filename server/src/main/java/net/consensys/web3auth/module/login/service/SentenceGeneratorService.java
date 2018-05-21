package net.consensys.web3auth.module.login.service;

import net.consensys.web3auth.module.login.model.LoginSentence;

/**
 * Generate and store a unique sentence that will be signed by the client
 * 
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public interface SentenceGeneratorService {

    /**
     * Generate a unique sentence and store it
     * @return sentence
     */
    LoginSentence generateSentence(String appId, Long expiration);
    
    /**
     * Retrieve a unique sentence from the storage
     * @param id
     * @return sentence
     */
    LoginSentence getSentencce(String id);
    
    /**
     * Disable a sentence after use
     * @param id
     */
    void disableSentence(String id);
    
    /**
     * Disabled expired sentence
     */
    void scheduleAutoDisablingExpiredSentence();
    
}
