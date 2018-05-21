package net.consensys.web3auth.module.adapter.springsecurity;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class AuthenticationToken extends AbstractAuthenticationToken implements Authentication {

    private static final long serialVersionUID = 620427322160440219L;
    
    private final Object principal;
    
    public AuthenticationToken(Object principal) {
        this(principal, null);
    }
    
    public AuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        super.setAuthenticated(true);
        this.principal = principal;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

}
