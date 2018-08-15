/**
 * 
 */
package net.consensys.web3auth.module.authority.service;

import java.util.List;

import net.consensys.web3auth.common.dto.Organisation;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public interface AuthorityService {

    List<Organisation> getOrganisation(String address);
    
}
