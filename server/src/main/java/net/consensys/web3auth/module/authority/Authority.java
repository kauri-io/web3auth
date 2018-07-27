package net.consensys.web3auth.module.authority;

import java.util.List;

public interface Authority {
    public List<String> getOrganisations(String contractAddress, String user);
    public List<String> getPrivileges(String contractAddress, String user, String organisation);
}
