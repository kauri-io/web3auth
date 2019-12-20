package net.consensys.web3auth.service.login.otc.inmemory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.common.dto.OTC;
import net.consensys.web3auth.service.admin.ConfigService;
import net.consensys.web3auth.service.login.otc.OTCGeneratorService;

@Slf4j
@Service
@ConditionalOnProperty(name = "web3auth.otc.type", havingValue = "IN_MEMORY", matchIfMissing=true)
public class InMemoryOTCGeneratorService implements OTCGeneratorService {

    private final Map<String, OTC> storage = new HashMap<>();
    private final String dictionnary;
    private final int length;
    private final String prefix;
    private final long expiration;
    
    @Autowired
    public InMemoryOTCGeneratorService(ConfigService configService) {
        this.dictionnary = configService.getOtc().getDictionnary();
        this.length = configService.getOtc().getLength();
        this.prefix = configService.getOtc().getPrefix();
        this.expiration = configService.getOtc().getExpiration();
    }
    
    @Override
    public OTC generateOTC(String appId) {
        log.debug("generateOTC()");
        
        OTC s = new OTC(appId, OTCGeneratorService.generate(prefix, length, dictionnary), expiration);
        storage.put(s.getId(), s);
        
        log.debug("generateOTC(): {}", s);
        return s;
    }

    @Override
    public Optional<OTC> getOTC(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public void disableOTC(String id) {
        log.debug("disableOTC(id: {})", id);
        
        storage.get(id).setActive(false);
    }

    @Override
    @Scheduled(fixedRate = 10000)
    public void scheduleAutoDisablingExpiredOTC() {
        log.debug("scheduleAutoDisablingExpiredOTC()");
        
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
