package net.consensys.web3auth.module.login.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
    private String address;
    
    @NotNull 
    @Size(min=1)
    private String signature;
    
    @NotNull
    @Size(min=1)
    private String sentenceId;

    @NotNull 
    @Size(min=1)
    private String appId;
    
    @NotNull 
    @Size(min=1)
    private String clientId;

    private String redirectUri;
    
    public LoginRequest(String appId, String clientId, String sentenceId, String redirectUri) {
        this.sentenceId = sentenceId;
        this.redirectUri = redirectUri;
        this.appId = appId;
        this.clientId = clientId;
    }
    
}
