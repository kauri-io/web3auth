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
    private String sentenceId;

    @NotNull 
    @Size(min=1)
    @JsonProperty("app_id")
    private String appId;
    
    @NotNull 
    @Size(min=1)
    @JsonProperty("client_id")
    private String clientId;

    private String redirectUri;
    
    /**
     * Constructor to prepare the request (not validated)
     */
    public LoginRequest(String appId, String clientId, String sentenceId, String redirectUri) {
        this.sentenceId = sentenceId;
        this.redirectUri = redirectUri;
        this.appId = appId;
        this.clientId = clientId;
    }
    
}
