package net.consensys.web3auth.configuration;

import org.springframework.stereotype.Component;

import net.consensys.web3auth.common.Constant;
import net.consensys.web3auth.common.dto.AccountDetails;
import net.consensys.web3auth.common.dto.ClientDetails;
import net.consensys.web3auth.common.service.Web3AuthWSClient;
import net.consensys.web3auth.controller.AccountController;
import net.consensys.web3auth.controller.AdminControler;

@Component
public class Web3AuthWSShortCircuitClient implements Web3AuthWSClient {

    private final AdminControler adminControler;
    private final AccountController accountController;
    
    public Web3AuthWSShortCircuitClient(AdminControler adminControler, AccountController accountController) {
        this.adminControler = adminControler;
        this.accountController = accountController;
    }
    
    @Override
    public ClientDetails getClient() {
        return adminControler.getClientDetails(Constant.CLIENT_ID_SERVER);
    }

    @Override
    public AccountDetails getAccountByToken(String token) {
        return accountController.validateToken(token);
    }

}
