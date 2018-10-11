/**
 * 
 */
package net.consensys.web3auth.module.authority.service.cache.db;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.consensys.web3auth.common.dto.Organisation;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
@Document
@NoArgsConstructor @ToString
public class UserDomain {

    private @Id @Getter @Setter String id;
    private @Getter @Setter Set<Organisation> orgs = new HashSet<>();

    public UserDomain(String id) {
        this.id = id;
    }
    
    public UserDomain(String id, Organisation org) {
        this.id = id;
        this.orgs.add(org);
    }
    
}
