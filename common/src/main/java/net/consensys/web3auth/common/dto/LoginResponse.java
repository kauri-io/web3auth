package net.consensys.web3auth.common.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginResponse {

    private String clientId;
    private String account;
    private String wallet;
    private String token;
    private Date expiration;
    

    
}
