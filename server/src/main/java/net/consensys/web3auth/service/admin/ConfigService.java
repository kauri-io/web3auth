package net.consensys.web3auth.service.admin;

import net.consensys.web3auth.configuration.Web3AuthSettings.Client;
import net.consensys.web3auth.configuration.Web3AuthSettings.CookieSetting;
import net.consensys.web3auth.configuration.Web3AuthSettings.JwtSetting;
import net.consensys.web3auth.configuration.Web3AuthSettings.OTCSetting;
import net.consensys.web3auth.configuration.Web3AuthSettings.SocialConnect;
import net.consensys.web3auth.configuration.Web3AuthSettings.WalletSetting;

public interface ConfigService {

    String getInstance();
    String getServerUrl();
    JwtSetting getJwt();
    OTCSetting getOtc();
    CookieSetting getCookie();
    Client getClient(String clientId);
    WalletSetting getWalletSetting();
    SocialConnect getSocialConnect();
}
