package net.consensys.web3auth.module.wallet.integration.relay;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RelayWalletRemoveKeyRequest {

    private String signature;
    
}
