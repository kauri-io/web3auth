package net.consensys.web3auth.module.adapter.springsecurity.authentication;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Getter;
import net.consensys.web3auth.module.adapter.springsecurity.AuthenticationToken;
import net.consensys.web3auth.module.adapter.springsecurity.deserialization.AuthenticationTokenSerializer;

@JsonDeserialize(using = AuthenticationTokenSerializer.class)
public class IdentifiedAuthenticationToken extends AbstractAuthenticationToken implements AuthenticationToken{
    private static final long serialVersionUID = -1816479111424334101L;

    public static final String TYPE = "IDENTIFIED_AUTHENTICATION_TOKEN";
    
    private final @Getter String type;
    private final @Getter String principal;
    private final @Getter String remoteAddress;
    private final @Getter String token;

    public IdentifiedAuthenticationToken() {
        this(null, null, null);
    }
    
    public IdentifiedAuthenticationToken(String principal, String token, String remoteAddress) {
        this(principal, token, remoteAddress, null);
    }

    public IdentifiedAuthenticationToken(String principal, String token, String remoteAddress, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        super.setAuthenticated(true);
        this.type = TYPE;
        this.principal = principal;
        this.token = token;
        this.remoteAddress = remoteAddress;
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
        IdentifiedAuthenticationToken other = (IdentifiedAuthenticationToken) obj;
        if (principal == null) {
            if (other.principal != null)
                return false;
        } else if (!principal.equals(other.principal))
            return false;
        return true;
    }

}
