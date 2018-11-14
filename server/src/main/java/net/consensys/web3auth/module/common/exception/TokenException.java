/**
 * 
 */
package net.consensys.web3auth.module.common.exception;

import lombok.Getter;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public class TokenException extends RuntimeException {

    private static final long serialVersionUID = 3028268244646575409L;

    private final @Getter String token;
    
    public TokenException(String token, Throwable exception) {
        this(token, null, exception);
    }
    
    public TokenException(String token, String msg) {
        this(token, msg, null);
    }
    
    public TokenException(String token, String msg, Throwable exception) {
        super(msg, exception);
        this.token = token;
    }
    
}
