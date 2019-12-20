package net.consensys.web3auth.module.adapter.springsecurity;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;
import net.consensys.web3auth.common.dto.AccountDetails;

public class Web3AuthAuthenticationToken extends AbstractAuthenticationToken implements Authentication {

    private static final long serialVersionUID = -6616097074300585861L;
    
    private final @Getter String principal;
    private final @Getter String remoteAddress;
    private final @Getter String token;
    private final @Getter AccountDetails account;

    public Web3AuthAuthenticationToken() {
        this(null, null, null, null);
    }
    
    public Web3AuthAuthenticationToken(AccountDetails account, String token, String remoteAddress) {
        this(account, token, remoteAddress, null);
    }

    public Web3AuthAuthenticationToken(AccountDetails account, String token, String remoteAddress, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        super.setAuthenticated(true);
        this.principal = account.getWallet();
        this.account = account;
        this.token = token;
        this.remoteAddress = remoteAddress;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((principal == null) ? 0 : principal.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Web3AuthAuthenticationToken other = (Web3AuthAuthenticationToken) obj;
        if (principal == null) {
            if (other.principal != null)
                return false;
        } else if (!principal.equals(other.principal))
            return false;
        return true;
    }

}
