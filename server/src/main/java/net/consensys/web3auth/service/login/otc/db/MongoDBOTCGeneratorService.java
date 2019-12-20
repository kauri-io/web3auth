package net.consensys.web3auth.service.login.otc.db;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.common.dto.OTC;
import net.consensys.web3auth.service.admin.ConfigService;
import net.consensys.web3auth.service.login.otc.OTCGeneratorService;

@Service
@Slf4j
@ConditionalOnProperty(name = "web3auth.otc.type", havingValue = "DB")
public class MongoDBOTCGeneratorService implements OTCGeneratorService {
    
    private final OTCRepository repository;
    private final String dictionnary;
    private final int length;
    private final String prefix;
    private final long expiration;
    
    @Autowired
    public MongoDBOTCGeneratorService(OTCRepository repository, ConfigService configService) {
        this.repository = repository;
        this.dictionnary = configService.getOtc().getDictionnary();
        this.length = configService.getOtc().getLength();
        this.prefix = configService.getOtc().getPrefix();
        this.expiration = configService.getOtc().getExpiration();
    }
    
    @Override
    public OTC generateOTC(String appId) {
        log.debug("generateOTC()");
        
        OTC s = new OTC(appId, OTCGeneratorService.generate(prefix, length, dictionnary), expiration);
        repository.save(s);
        
        log.debug("generateOTC(): {}", s);
        return s;
    }

    @Override
    public Optional<OTC> getOTC(String id) {
        return repository.findById(id);
    }

    @Override
    public void disableOTC(String id) {
        log.debug("disableOTC(id: {})", id);
        
        Optional<OTC> OTC = repository.findById(id);

        if (OTC.isPresent()) {
            OTC.get().setActive(false);
            repository.save(OTC.get());
        } else {
            log.error("Attempting to disable OTC that doesn't exist, with id: {}", id);
        }
    }

    @Override
    @Scheduled(fixedRate = 10000)
    public void scheduleAutoDisablingExpiredOTC() {
        log.debug("scheduleAutoDisablingExpiredOTC()");
        
        repository.findAll()
            .stream()
            .filter(
                    item -> item.isActive() 
                 && 
                    item.getDateExpiration().compareTo(Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC))) < 0)
            .forEach(item -> {
                log.trace("Sentence {} is expired", item.getId());
                item.setActive(false);
                repository.save(item);
            });
        
    }
}
