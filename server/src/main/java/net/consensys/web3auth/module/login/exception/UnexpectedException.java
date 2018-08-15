/**
 * 
 */
package net.consensys.web3auth.module.login.exception;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public class UnexpectedException extends RuntimeException {

    private static final long serialVersionUID = 4057681420448118620L;

    public UnexpectedException(Throwable e) {
        super(e);
    }
    
}
