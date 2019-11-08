package net.consensys.web3auth.module.login.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginResponse {

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("address")
    private String address;

    @JsonProperty("wallet_address")
    private String wallet_address;

    @JsonProperty("token")
    private String token;

    @JsonProperty("expiration")
    private Date expiration;
    

    
}
