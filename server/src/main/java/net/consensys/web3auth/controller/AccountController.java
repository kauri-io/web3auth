package net.consensys.web3auth.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.common.Constant;
import net.consensys.web3auth.common.JwtUtils;
import net.consensys.web3auth.common.dto.AccountDetails;
import net.consensys.web3auth.common.dto.ExecRequest;
import net.consensys.web3auth.common.dto.ExecResponse;
import net.consensys.web3auth.module.adapter.springsecurity.Web3AuthAuthenticationToken;
import net.consensys.web3auth.service.admin.ConfigService;
import net.consensys.web3auth.service.wallet.WalletService;
import net.consensys.web3auth.service.wallet.repository.Wallet;

@RestController
@RequestMapping("/account")
@Slf4j
public class AccountController {

    private final ConfigService configService;
    private final WalletService walletService;
    
    public AccountController(ConfigService configService, WalletService walletService) {
        this.configService = configService;
        this.walletService = walletService;
    }
    
    @PostMapping(value = "/token")
    public AccountDetails validateToken(@RequestBody String token) {
        log.debug("validateToken(token: {})", token.substring(0,20) + "*********************************************");
        
        JwtUtils.validateToken(configService.getJwt(), token);
        
        String account = JwtUtils.getUsernameFromToken(configService.getJwt(), token);
        String wallet = (String) JwtUtils.getCustomClaimFromToken(configService.getJwt(), token, Constant.CLAIM_WALLET);

        Wallet w = walletService.get(wallet);
        
        return new AccountDetails(wallet, account, w.getKeys(), w.getNonce());
    } 
    
    @GetMapping(value = "/")
    public AccountDetails getAccountDetails(Principal principal) {
        log.debug("getAccountDetails(account: {})", principal.getName());
        
        Web3AuthAuthenticationToken auth = (Web3AuthAuthenticationToken) principal;
        
        return auth.getAccount();
    }

    @PostMapping(value = "/exec/prepare")
    public ExecResponse prepareExec(Principal principal, @RequestBody ExecRequest request) {
        log.debug("prepareExec(account: {}, request: {})", principal.getName(), request);
        
        String txHash = walletService.prepareExec(principal.getName(), request.getTo(), request.getData());
        
        return new ExecResponse(txHash);
    }
    
    @PostMapping(value = "/exec")
    public ExecResponse exec(Principal principal, @RequestBody ExecRequest request) {
        log.debug("exec(account: {}, request: {})", principal.getName(), request);
        
        String txHash = walletService.exec(principal.getName(), request.getTo(), request.getData(), request.getSignature());
        
        return new ExecResponse(txHash);
    }
}
