package net.consensys.web3auth.module.wallet.model;

import lombok.Data;
import net.consensys.web3auth.module.wallet.model.Key.KeyRole;

@Data
public class AddKeyRequest {

    private String key;
    private KeyRole role;
    private String signature;
    
}
