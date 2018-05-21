package net.consensys.web3auth.module.login.model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LoginSentence {
    
    private final String id;
    private final String sentence;
    private final Date dateCreated;
    private final Date dateExpiration;
    private final String appId;
    private boolean active;
    
    public LoginSentence (String appId, String sentence, Long expiration) {
        this.id = UUID.randomUUID().toString();
        this.appId = appId;
        this.sentence = sentence;
        this.active = true;
        this.dateCreated = Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));
        this.dateExpiration = Date.from(LocalDateTime.now().plusSeconds(expiration).toInstant(ZoneOffset.UTC));
    }
}
