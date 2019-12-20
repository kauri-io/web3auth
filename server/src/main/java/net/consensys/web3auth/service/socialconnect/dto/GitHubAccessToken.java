package net.consensys.web3auth.service.socialconnect.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubAccessToken {

    private @JsonProperty("access_token") String accessToken;
    
}
