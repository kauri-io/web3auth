package net.consensys.web3auth.module.adapter.springsecurity;

import org.springframework.security.core.Authentication;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import net.consensys.web3auth.module.adapter.springsecurity.authentication.AnonymousAuthenticationToken;
import net.consensys.web3auth.module.adapter.springsecurity.authentication.IdentifiedAuthenticationToken;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true,
        defaultImpl = AnonymousAuthenticationToken.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = IdentifiedAuthenticationToken.class, name = IdentifiedAuthenticationToken.TYPE),
        @JsonSubTypes.Type(value = AnonymousAuthenticationToken.class, name = AnonymousAuthenticationToken.TYPE)
})
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface AuthenticationToken extends Authentication {

    String getType();
    String getPrincipal();
    String getRemoteAddress();

    @JsonIgnore
    default Object getCredentials() {
        return null;
    }

    @JsonIgnore
    default boolean isAuthenticated() {
        return true;
    }

    @JsonIgnore
    default Object getDetails() {
        return null;
    }
    
}
