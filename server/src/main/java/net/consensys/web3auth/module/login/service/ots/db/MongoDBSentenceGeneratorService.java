package net.consensys.web3auth.module.login.service.ots.db;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.module.login.model.LoginSentence;
import net.consensys.web3auth.module.login.service.ots.SentenceGeneratorService;

@Service
@Slf4j
public class MongoDBSentenceGeneratorService implements SentenceGeneratorService {

    private static final String ALPHA_CAPS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALPHA = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMERIC = "0123456789";

    private static final int LENGTH = 10;
    private static final SecureRandom random = new SecureRandom();
    
    private final SentenceRepository repository;
    
    @Autowired
    public MongoDBSentenceGeneratorService(SentenceRepository repository) {
        this.repository = repository;
    }
    
    @Override
    public LoginSentence generateSentence(String appId, Long expiration) {
        log.debug("generateSentence()");
        
        LoginSentence s = new LoginSentence(appId, generate(LENGTH, ALPHA_CAPS+ALPHA+NUMERIC), expiration);
        repository.save(s);
        
        log.debug("generateSentence(): {}", s);
        return s;
    }

    @Override
    public Optional<LoginSentence> getSentence(String id) {
        return Optional.ofNullable(repository.findOne(id));
    }

    @Override
    public void disableSentence(String id) {
        log.debug("disableSentence(id: {})", id);
        
        LoginSentence sentence = repository.findOne(id);
        sentence.setActive(false);
        repository.save(sentence);
    }

    @Override
    @Scheduled(fixedRate = 10000)
    public void scheduleAutoDisablingExpiredSentence() {
        log.debug("scheduleAutoDisablingExpiredSentence()");
        
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
    
    private static String generate(int len, String dic) {
        return IntStream.range(0, len)
            .mapToObj( i ->  dic.charAt(random.nextInt(dic.length())) )
            .map(String::valueOf)
            .collect(Collectors.joining());
    }

}
