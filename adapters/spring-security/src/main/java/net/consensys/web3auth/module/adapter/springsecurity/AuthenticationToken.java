package net.consensys.web3auth.module.adapter.springsecurity;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AuthenticationToken extends AbstractAuthenticationToken implements Authentication {

    private static final long serialVersionUID = 620427322160440219L;
    
    private final String principal;
    private final String token;

    public AuthenticationToken() {
        this(null);
    }
    
    public AuthenticationToken(String principal) {
        this(principal, null);
    }
    
    public AuthenticationToken(String principal, String token) {
        this(principal, token, null);
    }

    public AuthenticationToken(String principal, String token, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        super.setAuthenticated(true);
        this.principal = principal;
        this.token = token;
    }

    public String getName(boolean remove0x) {
        Assert.notNull(super.getName(), "name can't be null");
        
        if(remove0x && super.getName().startsWith("0x")) {
            return super.getName().substring(2, super.getName().length());
        }
        
        return super.getName();
    }

    @JsonIgnore
    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public String getPrincipal() {
        return principal;
    }

    public String getToken() {
        return token;
    }

    @JsonIgnore
    @Override
    public boolean isAuthenticated() {
        return super.isAuthenticated();
    }

    @JsonIgnore
    @Override
    public Object getDetails() {
        return super.getDetails();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((token == null) ? 0 : token.hashCode());
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
        AuthenticationToken other = (AuthenticationToken) obj;
        if (token == null) {
            if (other.token != null)
                return false;
        } else if (!token.equals(other.token))
            return false;
        return true;
    }
}
