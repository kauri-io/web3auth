/**
 * 
 */
package net.consensys.web3auth.exception;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public class OTCExpiredException extends RuntimeException {

    private static final long serialVersionUID = -1037030700282193086L;

    public OTCExpiredException(String sentenceId) {
        super("Sentence [id: "+sentenceId+"] expired");
    }
    
}
