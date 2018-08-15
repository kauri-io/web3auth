/**
 * 
 */
package net.consensys.web3auth.module.common.exception;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public class ExpiredTokenException extends RuntimeException {

    private static final long serialVersionUID = 3028268244646575409L;

    public ExpiredTokenException(Throwable t) {
        super(t);
    }
    
}
