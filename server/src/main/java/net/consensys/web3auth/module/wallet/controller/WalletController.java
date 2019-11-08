package net.consensys.web3auth.module.wallet.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.consensys.web3auth.module.wallet.model.AddKeyRequest;
import net.consensys.web3auth.module.wallet.model.Wallet;
import net.consensys.web3auth.module.wallet.service.WalletService;

@RestController
@RequestMapping("/api/wallet")
@ConditionalOnProperty(name = "web3auth.wallet.enable", havingValue = "true")
public class WalletController {

    private final WalletService walletService;
    
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }
    
    @RequestMapping(value = "/{wallet}", method = RequestMethod.GET)
    public Wallet getWallet(@PathVariable String wallet) {
        return walletService.get(wallet);
    } 
    
    @RequestMapping(value = "/{wallet}/key", method = RequestMethod.POST)
    public void addKey(
            @PathVariable String wallet,
            @RequestBody AddKeyRequest request) {
        walletService.addKey(wallet, request.getKey(), request.getRole(), request.getSignature());
    }
    
}
