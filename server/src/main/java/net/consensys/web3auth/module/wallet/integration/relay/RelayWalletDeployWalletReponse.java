package net.consensys.web3auth.module.wallet.integration.relay;

import lombok.Data;

@Data
public class RelayWalletDeployWalletReponse {

    private String tx;
    private String key;
    private String wallet;
    
}
