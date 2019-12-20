/**
 * 
 */
package net.consensys.web3auth.exception;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public class WalletCreationtException extends RuntimeException {

    private static final long serialVersionUID = -3734039218077909105L;
    
    public WalletCreationtException(String message, Throwable ex) {
        super(message, ex);
    }

}
