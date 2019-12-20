package net.consensys.web3auth.common.dto;

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
    private String account;
    
    @NotNull 
    @Size(min=1)
    private String signature;
    
    @NotNull
    @Size(min=1)
    private String otcId;

    @NotNull 
    @Size(min=1)
    private String clientId;

    @NotNull 
    @Size(min=1) //TODO enum?
    private String provider;

    private String redirectUri;
    
    /**
     * Constructor to prepare the request (not validated)
     */
    public LoginRequest(String clientId, String otcId, String redirectUri) {
        this.otcId = otcId;
        this.redirectUri = redirectUri;
        this.clientId = clientId;
    }
    
}
