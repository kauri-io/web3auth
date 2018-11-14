/**
 * 
 */
package net.consensys.web3auth.common.dto.exception;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public class HTTPClientException extends RuntimeException {

    private static final long serialVersionUID = 5292527778642409245L;

    public HTTPClientException(String msg) {
        super(msg);
    }
    
}
