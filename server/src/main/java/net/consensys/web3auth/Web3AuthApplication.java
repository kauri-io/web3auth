package net.consensys.web3auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableScheduling
@EnableWebMvc
public class Web3AuthApplication  {

    public static void main(String[] args) {
        SpringApplication.run(Web3AuthApplication .class, args);
    }
    

}