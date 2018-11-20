package net.consensys.web3auth.module.login.service.ots.db;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.module.login.model.OTS;
import net.consensys.web3auth.module.login.service.ots.OTSGeneratorService;

@Service
@Slf4j
@ConditionalOnProperty(name = "web3auth.otp.type", havingValue = "DB")
public class MongoDBOTPGeneratorService implements OTSGeneratorService {
    
    private final OTPRepository repository;
    private final String dictionnary;
    private final int length;
    private final String prefix;
    private final long expiration;
    
    @Autowired
    public MongoDBOTPGeneratorService(
            OTPRepository repository,
            @Value("${web3auth.otp.dictionnary}") String dictionnary, 
            @Value("${web3auth.otp.length}") int length,
            @Value("${web3auth.otp.prefix}") String prefix,
            @Value("${web3auth.otp.expiration}") long expiration) {
        this.repository = repository;
        this.dictionnary = dictionnary;
        this.length = length;
        this.prefix = prefix;
        this.expiration = expiration;
    }
    
    @Override
    public OTS generateOTS(String appId) {
        log.debug("generateOTS()");
        
        OTS s = new OTS(appId, OTSGeneratorService.generate(prefix, length, dictionnary), expiration);
        repository.save(s);
        
        log.debug("generateOTS(): {}", s);
        return s;
    }

    @Override
    public Optional<OTS> getOTS(String id) {
        return repository.findById(id);
    }

    @Override
    public void disableOTS(String id) {
        log.debug("disableOTS(id: {})", id);
        
        Optional<OTS> ots = repository.findById(id);

        if (ots.isPresent()) {
            ots.get().setActive(false);
            repository.save(ots.get());
        } else {
            log.error("Attempting to disable OTS that doesn't exist, with id: {}", id);
        }
    }

    @Override
    @Scheduled(fixedRate = 10000)
    public void scheduleAutoDisablingExpiredOTS() {
        log.debug("scheduleAutoDisablingExpiredOTS()");
        
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
