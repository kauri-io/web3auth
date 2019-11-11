package net.consensys.web3auth.module.wallet.integration.relay;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import net.consensys.web3auth.module.wallet.integration.WalletIntegration;
import net.consensys.web3auth.module.wallet.model.Key.KeyRole;

@Component
@ConditionalOnProperty(name = "web3auth.wallet.mode", havingValue = "RELAY")
public class RelayWalletIntegration implements WalletIntegration {

    private final String endpoint;
    private final RestTemplate client;
    protected HttpHeaders defaultHeaders;
    
    public RelayWalletIntegration(@Value("${web3auth.wallet.relay.endpoint}") String endpoint) {
        this.endpoint = endpoint;
        this.client = new RestTemplate();
        
        this.defaultHeaders = new HttpHeaders();
        defaultHeaders.add("Content-Type", "application/json");
        defaultHeaders.add("Accept", "*/*");
    }
    
    @Override
    public String deployWallet(String key, String hash, String signature) {
        
        try {
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromUriString(endpoint + "/wallet");

            ResponseEntity<RelayWalletDeployWalletReponse> response = client.exchange(
                    builder.toUriString(), 
                    HttpMethod.POST, 
                    generateHttpEntity(new RelayWalletDeployWalletRequest(key, hash, signature)), 
                    RelayWalletDeployWalletReponse.class);
            
            return response.getBody().getWallet();
            
        } catch(HttpClientErrorException e) {
            throw handleHTTPExceptiion(e);
        }
    }

    @Override
    public void addKey(String wallet, String key, KeyRole role, String signature) {
        
        try {
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromUriString(endpoint + "/wallet/" + wallet + "/key");

            client.exchange(
                    builder.toUriString(), 
                    HttpMethod.POST, 
                    generateHttpEntity(new RelayWalletAddKeyRequest(key, role.code, signature)), 
                    RelayWalletGenericResponse.class);
            
        } catch(HttpClientErrorException e) {
            throw handleHTTPExceptiion(e);
        }
    }

    @Override
    public void removeKey(String wallet, String key, String signature) {
        
        try {
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromUriString(endpoint + "/wallet/" + wallet + "/key/" + key);

            client.exchange(
                    builder.toUriString(), 
                    HttpMethod.POST, 
                    generateHttpEntity(new RelayWalletRemoveKeyRequest(signature)), 
                    RelayWalletGenericResponse.class);
            
        } catch(HttpClientErrorException e) {
            throw handleHTTPExceptiion(e);
        }
    }

    @Override
    public Integer getNonce(String wallet) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromUriString(endpoint + "/wallet/" + wallet + "/nonce");

            ResponseEntity<RelayWalletGetNonceResponse> response = client.exchange(
                    builder.toUriString(), 
                    HttpMethod.GET, 
                    generateHttpEntity(), 
                    RelayWalletGetNonceResponse.class);
            
            return response.getBody().getNonce();
            
        } catch(HttpClientErrorException e) {
            throw handleHTTPExceptiion(e);
        }
    }

    protected <T> HttpEntity<T> generateHttpEntity(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(defaultHeaders);
        
        return new HttpEntity<>(body, headers);
    }

    protected <T> HttpEntity<T> generateHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(defaultHeaders);
        
        return new HttpEntity<>(headers);
    }
    
    protected RuntimeException handleHTTPExceptiion(HttpClientErrorException e) {

        HttpStatus status = e.getStatusCode();
        if (status == HttpStatus.NOT_FOUND) { 
            throw new RuntimeException(e.getResponseBodyAsString());
        } else  if (status == HttpStatus.BAD_REQUEST) { 
            throw new RuntimeException(e.getResponseBodyAsString());
        } else {
            throw new RuntimeException(e.getResponseBodyAsString());
        }
    }

    
}
