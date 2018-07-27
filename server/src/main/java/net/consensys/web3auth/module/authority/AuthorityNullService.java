package net.consensys.web3auth.module.authority;

import java.util.List;

public class AuthorityNullService implements Authority {

    @Override
    public List<String> getOrganisations(String contractAddress, String user) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getPrivileges(String contractAddress, String user, String organisation) {
        // TODO Auto-generated method stub
        return null;
    }

}
