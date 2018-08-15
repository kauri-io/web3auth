/**
 * 
 */
package net.consensys.web3auth.module.login.exception;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public class SentenceExpiredException extends RuntimeException {

    private static final long serialVersionUID = -1037030700282193086L;

    public SentenceExpiredException(String sentenceId) {
        super("Sentence [id: "+sentenceId+"] expired");
    }
    
}
