package net.consensys.web3auth.service.socialconnect.repository;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class SocialConnectCSRF {

    private @Id String csrf;
    private String clientId;
    private String redirectUri;
    private Date dateCreated;
    
    public SocialConnectCSRF(String clientId, String redirectUri) {
        this.csrf = UUID.randomUUID().toString();
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.dateCreated = new Date();
    }
    
    
    
}