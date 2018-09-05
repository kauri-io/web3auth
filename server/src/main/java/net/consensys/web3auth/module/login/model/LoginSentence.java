package net.consensys.web3auth.module.login.model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor @NoArgsConstructor
@ToString
@Document
public class LoginSentence {

    @Id
    @Field("id")
    @JsonProperty("id")
    private String id;

    @Field("sentence")
    @JsonProperty("sentence")
    private String sentence;

    @Field("date_created")
    @JsonProperty("date_created")
    private Date dateCreated;

    @Field("date_expiration")
    @JsonProperty("date_expiration")
    private Date dateExpiration;

    @Field("app_id")
    @JsonProperty("app_id")
    private String appId;

    @Field("active")
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
