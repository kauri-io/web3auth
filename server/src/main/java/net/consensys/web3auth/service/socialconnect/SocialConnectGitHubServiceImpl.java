package net.consensys.web3auth.service.socialconnect;

import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;
import net.consensys.web3auth.service.admin.ConfigService;
import net.consensys.web3auth.service.socialconnect.dto.GitHubAccessToken;
import net.consensys.web3auth.service.socialconnect.dto.GitHubUser;
import net.consensys.web3auth.service.socialconnect.repository.SocialConnectAccountRepository;
import net.consensys.web3auth.service.socialconnect.repository.SocialConnectCSRF;
import net.consensys.web3auth.service.socialconnect.repository.SocialConnectCSRFRepository;

@Service
@Slf4j
public class SocialConnectGitHubServiceImpl extends AbstractSocialConnectService implements SocialConnectGitHubService {
    
    private static final String GITHUB_AUTHORIZE_URL = "https://github.com/login/oauth/authorize";
    private static final String GITHUB_ACCESS_TOKEN_URL = "https://github.com/login/oauth/access_token";
    private static final String GITHUB_API_USER_URL = "https://api.github.com/user";
    

    @Autowired
    public SocialConnectGitHubServiceImpl(ConfigService configService, SocialConnectAccountRepository socialConnectAccountRepository,
            SocialConnectCSRFRepository socialConnectCSRFRepository) {
        super(configService, socialConnectAccountRepository, socialConnectCSRFRepository);
    }
    
    @Override
    public String connect(String clientId, String redirectUri) {
       
        SocialConnectCSRF csrf = new SocialConnectCSRF(clientId, redirectUri);
        socialConnectCSRFRepository.save(csrf);
        
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(GITHUB_AUTHORIZE_URL)
                .queryParam("client_id", configService.getSocialConnect().getGithub().getClientId())
                .queryParam("redirect_uri", configService.getSocialConnect().getGithub().getRedirect())
                .queryParam("state", csrf.getCsrf()) 
                .queryParam("scope", "user:email");
        
        return builder.toUriString();
    }

    @Override
    public String redirect(String code, String state, HttpServletResponse response) {
        
        try {
            
            Optional<SocialConnectCSRF> csrf = socialConnectCSRFRepository.findById(state);
            if(!csrf.isPresent()) {
                log.error("No CSRF found (state: {})", state);
                throw new RuntimeException("No CSRF found");
            }
            
            // Get access_token from code
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromUriString(GITHUB_ACCESS_TOKEN_URL)
                    .queryParam("client_id", configService.getSocialConnect().getGithub().getClientId())
                    .queryParam("client_secret", configService.getSocialConnect().getGithub().getClientSecret())
                    .queryParam("redirect_uri", configService.getSocialConnect().getGithub().getRedirect())
                    .queryParam("state", csrf.get().getCsrf()) 
                    .queryParam("code", code);

            ResponseEntity<GitHubAccessToken> gitHubAccessTokenResponse = restTemplate.postForEntity(
                    builder.toUriString(), 
                    null, 
                    GitHubAccessToken.class);
            log.debug("gitHubAccessTokenResponse: {}", gitHubAccessTokenResponse.getBody());
            
            // Get user details
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "token " + gitHubAccessTokenResponse.getBody().getAccessToken());
            headers.add("User-Agent", "Login-App");
            ResponseEntity<GitHubUser> gitHubUserResponse = restTemplate.exchange(
                    GITHUB_API_USER_URL, 
                    HttpMethod.GET, 
                    new HttpEntity<>(headers), 
                    GitHubUser.class);
            log.debug("gitHubUserResponse: {}", gitHubUserResponse.getBody());
            
            return generateNewSocialConnectAccount(
                    gitHubUserResponse.getBody().getEmail(), 
                    "github", 
                    csrf.get().getClientId(),
                    csrf.get().getRedirectUri(),
                    gitHubUserResponse.getBody().getLogin(), 
                    response);
            
            
        } catch(HttpClientErrorException ex) {
            log.error("Error", ex);
            throw ex;
        }
    }

}
