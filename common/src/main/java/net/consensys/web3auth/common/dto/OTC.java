package net.consensys.web3auth.common.dto;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class OTC {

    private @Id String id;
    private String instance;
    private String code;
    private boolean active;
    private Date dateCreated;
    private Date dateExpiration;
    
    public OTC (String instance, String code, Long expiration) {
        this.id = UUID.randomUUID().toString();
        this.instance = instance;
        this.code = code;
        this.active = true;
        this.dateCreated = Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));
        this.dateExpiration = Date.from(LocalDateTime.now().plusSeconds(expiration).toInstant(ZoneOffset.UTC));
    }
}
