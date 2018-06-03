    package net.consensys.web3auth.module.login.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.module.login.model.LoginSentence;

@Service
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
    public LoginSentence getSentencce(String id) {
        return storage.get(id);
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
        String result = "";
        for (int i = 0; i < len; i++) {
            int index = random.nextInt(dic.length());
            result += dic.charAt(index);
        }
        return result;
    }

}
