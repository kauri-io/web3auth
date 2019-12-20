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
public class GitHubUser {

    // see https://developer.github.com/v3/users/#response-with-public-and-private-profile-information
    private @JsonProperty("login") String login; //github username
    private @JsonProperty("id") String id; // github user id
    private @JsonProperty("email") String email;
    private @JsonProperty("avatar_url") String avatarUrl;
    private @JsonProperty("url") String url;
    
}
