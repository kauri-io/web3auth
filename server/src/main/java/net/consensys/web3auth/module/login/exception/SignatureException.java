/**
 * 
 */
package net.consensys.web3auth.module.login.exception;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public class SignatureException extends RuntimeException {

    private static final long serialVersionUID = -3734039218077909105L;
    
    public SignatureException(String message) {
        super(message);
    }

}
