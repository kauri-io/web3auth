package net.consensys.web3auth.service.wallet.repository;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class Wallet {
    
    private @Id String address;
    private @Transient Integer nonce;
    private List<String> keys;
}
