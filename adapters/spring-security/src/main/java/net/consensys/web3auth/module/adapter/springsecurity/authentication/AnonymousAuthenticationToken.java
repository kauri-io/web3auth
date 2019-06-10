package net.consensys.web3auth.module.adapter.springsecurity.authentication;

import java.util.UUID;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import lombok.Getter;
import net.consensys.web3auth.module.adapter.springsecurity.AuthenticationToken;

public class AnonymousAuthenticationToken  extends AbstractAuthenticationToken implements AuthenticationToken{

    private static final long serialVersionUID = -1239746271094554440L;

    public static final String TYPE = "ANONYMOUS_AUTHENTICATION_TOKEN";
    
    private final @Getter String type;
    private final @Getter String principal;
    private final @Getter String remoteAddress;

    public AnonymousAuthenticationToken() {
        this(null);
    }
    public AnonymousAuthenticationToken(String remoteAddress) {
        super(null);
        super.setAuthenticated(true);
        this.type = TYPE;
        this.principal = UUID.randomUUID().toString();
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
        AnonymousAuthenticationToken other = (AnonymousAuthenticationToken) obj;
        if (principal == null) {
            if (other.principal != null)
                return false;
        } else if (!principal.equals(other.principal))
            return false;
        return true;
    }
    
    
}
