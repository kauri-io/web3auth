package net.consensys.web3auth.module.login.service.ots.inmemory;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.module.login.model.LoginSentence;
import net.consensys.web3auth.module.login.service.ots.SentenceGeneratorService;

@Slf4j
public class InMemorySentenceGeneratorService implements SentenceGeneratorService {

    private static final String ALPHA_CAPS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALPHA = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMERIC = "0123456789";

    private static final int LENGTH = 10;
    private static final SecureRandom random = new SecureRandom();
    
    private final Map<String, LoginSentence> storage;
    
    @Autowired
    public InMemorySentenceGeneratorService() {
        storage = new HashMap<>();
    }
    
    @Override
    public LoginSentence generateSentence(String appId, Long expiration) {
        log.debug("generateSentence()");
        
        LoginSentence s = new LoginSentence(appId, generate(LENGTH, ALPHA_CAPS+ALPHA+NUMERIC), expiration);
        storage.put(s.getId(), s);
        
        log.debug("generateSentence(): {}", s);
        return s;
    }

    @Override
    public Optional<LoginSentence> getSentence(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public void disableSentence(String id) {
        log.debug("disableSentence(id: {})", id);
        
        storage.get(id).setActive(false);
    }

    @Override
    @Scheduled(fixedRate = 10000)
    public void scheduleAutoDisablingExpiredSentence() {
        log.debug("scheduleAutoDisablingExpiredSentence()");
        
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
    
    private static String generate(int len, String dic) {
        return IntStream.range(0, len)
            .mapToObj( i ->  dic.charAt(random.nextInt(dic.length())) )
            .map(String::valueOf)
            .collect(Collectors.joining());
    }

}
