package net.consensys.web3auth.module.login.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginRequest {
    
    @NotNull 
    @Size(min=40, max=42)
    @JsonProperty("address")
    private String address;
    
    @NotNull 
    @Size(min=1)
    @JsonProperty("signature")
    private String signature;
    
    @NotNull
    @Size(min=1)
    @JsonProperty("sentence_id")
    private String otsId;

    @NotNull 
    @Size(min=1)
    @JsonProperty("client_id")
    private String clientId;

    private String redirectUri;
    
    /**
     * Constructor to prepare the request (not validated)
     */
    public LoginRequest(String clientId, String otsId, String redirectUri) {
        this.otsId = otsId;
        this.redirectUri = redirectUri;
        this.clientId = clientId;
    }
    
}
