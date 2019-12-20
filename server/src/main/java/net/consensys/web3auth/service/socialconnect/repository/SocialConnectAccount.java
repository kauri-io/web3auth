package net.consensys.web3auth.service.socialconnect.repository;

import java.util.Map;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class SocialConnectAccount {

    private @Id String accountId;
    private String email;
    private String pk;
    private Map<String, String> socialHandles;
}