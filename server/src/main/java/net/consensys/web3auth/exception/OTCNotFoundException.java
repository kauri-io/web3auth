/**
 * 
 */
package net.consensys.web3auth.exception;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public class OTCNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -1037030700282193086L;

    public OTCNotFoundException(String sentenceId) {
        super("Sentence [id: "+sentenceId+"] not found");
    }
    
}
