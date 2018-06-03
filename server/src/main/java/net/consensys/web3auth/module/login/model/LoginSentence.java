package net.consensys.web3auth.module.login.model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LoginSentence {
    
    @JsonProperty("id")
    private final String id;
    
    @JsonProperty("sentence")
    private final String sentence;
    
    @JsonProperty("date_created")
    private final Date dateCreated;

    @JsonProperty("date_expiration")
    private final Date dateExpiration;
    
    @JsonProperty("app_id")
    private final String appId;
    
    @JsonProperty("active")
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
