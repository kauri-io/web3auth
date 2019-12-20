/**
 * 
 */
package net.consensys.web3auth.exception;

import java.util.Collection;

import net.consensys.web3auth.common.dto.ClientType;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public class WrongClientTypeException extends RuntimeException {

    private static final long serialVersionUID = -1697563926022903697L;

    public WrongClientTypeException(String clientId, ClientType actual, Collection<ClientType> expected) {
        super("Wrong type for client [id: "+clientId+"]. Actual " + actual + ", expected " + expected);
    }
}
