package net.consensys.web3auth.service.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.consensys.web3auth.common.Constant;
import net.consensys.web3auth.common.dto.ClientType;
import net.consensys.web3auth.configuration.Web3AuthSettings;
import net.consensys.web3auth.configuration.Web3AuthSettings.Client;
import net.consensys.web3auth.configuration.Web3AuthSettings.CookieSetting;
import net.consensys.web3auth.configuration.Web3AuthSettings.JwtSetting;
import net.consensys.web3auth.configuration.Web3AuthSettings.OTCSetting;
import net.consensys.web3auth.configuration.Web3AuthSettings.SocialConnect;
import net.consensys.web3auth.configuration.Web3AuthSettings.WalletSetting;
import net.consensys.web3auth.exception.ClientNotFound;


@Service
public class ConfigServiceImpl implements ConfigService {
    
    private final Web3AuthSettings settings;
    
    @Autowired
    public ConfigServiceImpl(Web3AuthSettings settings) {
        this.settings = settings;
    }

    @Override
    public String getInstance() {
        return settings.getInstance();
    }

    @Override
    public String getServerUrl() {
        return settings.getServerUrl();
    }
    
    @Override
    public JwtSetting getJwt() {
        return settings.getJwt();
    }
    
    @Override
    public OTCSetting getOtc() {
        return settings.getOtc();
    }

    @Override
    public CookieSetting getCookie() {
        return settings.getCookie();
    }
    
    @Override
    public Client getClient(String clientId) {
        
        if(clientId.equals(Constant.CLIENT_ID_SERVER)) {
            Client server = new Client();
            server.setClientId(Constant.CLIENT_ID_SERVER);
            server.setType(ClientType.BOTH);
            server.setDefaultRedirect(null);
            return server;
        }
        
        return settings
                .getClients()
                .stream()
                .filter(client -> client.getClientId().equals(clientId))
                .findFirst()
                .orElseThrow(() -> new ClientNotFound(clientId));
    }

    @Override
    public WalletSetting getWalletSetting() {
        return settings.getWallet();
    }

    @Override
    public SocialConnect getSocialConnect() {
        return settings.getSocialConnect();
    }
    
}
