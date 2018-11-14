/**
 * 
 */
package net.consensys.web3auth.common.dto.exception;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public class HTTPServerException extends RuntimeException {

    private static final long serialVersionUID = 5292527778642409245L;

    public HTTPServerException(String msg) {
        super(msg);
    }
    
}
