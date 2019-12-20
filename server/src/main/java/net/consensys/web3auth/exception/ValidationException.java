/**
 * 
 */
package net.consensys.web3auth.exception;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = -1697563926022903697L;

    public ValidationException(String message) {
        super(message);
    }
}
