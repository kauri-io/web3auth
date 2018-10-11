package net.consensys.web3auth.module.login.service.ots.inmemory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.module.login.model.OTS;
import net.consensys.web3auth.module.login.service.ots.OTSGeneratorService;

@Slf4j
@Service
@ConditionalOnProperty(name = "web3auth.otp.type", havingValue = "IN_MEMORY", matchIfMissing=true)
public class InMemoryOTSGeneratorService implements OTSGeneratorService {

    private final Map<String, OTS> storage = new HashMap<>();
    private final String dictionnary;
    private final int length;
    private final String prefix;
    private final long expiration;
    
    @Autowired
    public InMemoryOTSGeneratorService(
            @Value("${web3auth.otp.dictionnary}") String dictionnary, 
            @Value("${web3auth.otp.length}") int length,
            @Value("${web3auth.otp.prefix}") String prefix,
            @Value("${web3auth.otp.expiration}") long expiration) {
        this.dictionnary = dictionnary;
        this.length = length;
        this.prefix = prefix;
        this.expiration = expiration;
    }
    
    @Override
    public OTS generateOTS(String appId) {
        log.debug("generateOTS()");
        
        OTS s = new OTS(appId, OTSGeneratorService.generate(prefix, length, dictionnary), expiration);
        storage.put(s.getId(), s);
        
        log.debug("generateOTS(): {}", s);
        return s;
    }

    @Override
    public Optional<OTS> getOTS(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public void disableOTS(String id) {
        log.debug("disableOTS(id: {})", id);
        
        storage.get(id).setActive(false);
    }

    @Override
    @Scheduled(fixedRate = 10000)
    public void scheduleAutoDisablingExpiredOTS() {
        log.debug("scheduleAutoDisablingExpiredOTS()");
        
        storage.entrySet()
            .stream()
            .filter(
                    item -> item.getValue().isActive() 
                 && 
                    item.getValue().getDateExpiration().compareTo(Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC))) < 0)
            .forEach(item -> {
                log.trace("Sentence {} is expired", item.getKey());
                item.getValue().setActive(false);
            });
        
    }

}
