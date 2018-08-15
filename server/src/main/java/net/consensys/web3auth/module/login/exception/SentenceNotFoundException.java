/**
 * 
 */
package net.consensys.web3auth.module.login.exception;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public class SentenceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -1037030700282193086L;

    public SentenceNotFoundException(String sentenceId) {
        super("Sentence [id: "+sentenceId+"] not found");
    }
    
}
