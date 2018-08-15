/**
 * 
 */
package net.consensys.web3auth.module.common.exception;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public class InvalidTokenException extends RuntimeException {

    private static final long serialVersionUID = 3028268244646575409L;

    public InvalidTokenException(Throwable t) {
        super(t);
    }
    
}
