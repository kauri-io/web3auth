package net.consensys.web3auth.module.wallet.integration;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RelayWalletAddKeyRequest {

    private String key;
    private Integer role;
    private String signature;
    
}
