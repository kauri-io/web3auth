package net.consensys.web3auth.module.application.service;

import net.consensys.web3auth.module.application.model.Application.AuthoritySetting;
import net.consensys.web3auth.module.application.model.Application.Client;
import net.consensys.web3auth.module.application.model.Application.CookieSetting;
import net.consensys.web3auth.module.application.model.Application.JwtSetting;
import net.consensys.web3auth.module.application.model.Application.OTSSetting;

public interface ApplicationService {

    String getAppId();
    JwtSetting getJwt();
    OTSSetting getOts();
    AuthoritySetting getAuthority();
    CookieSetting getCookie();
    Client getClient(String clientId);

}
