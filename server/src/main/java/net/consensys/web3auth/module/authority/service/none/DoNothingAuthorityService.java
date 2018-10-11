package net.consensys.web3auth.module.authority.service.none;

import java.util.Collections;
import java.util.Set;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import net.consensys.web3auth.common.dto.Organisation;
import net.consensys.web3auth.module.authority.service.AuthorityService;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
@Service
@ConditionalOnProperty(name = "web3auth.authority.mode", havingValue = "NONE", matchIfMissing=true)
public class DoNothingAuthorityService implements AuthorityService {

    @Override
    public Set<Organisation> getOrganisation(String address) {
        return Collections.emptySet();
    }
}
