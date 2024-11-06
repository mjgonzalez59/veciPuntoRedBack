package com.puntored.veciapi.service;

import com.puntored.veciapi.config.ApiCredentialsConfig;
import com.puntored.veciapi.model.AuthToken;
import com.puntored.veciapi.model.UserCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PuntoRedAuthenticationService {

    @Autowired
    @Qualifier("authenticationRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private ApiCredentialsConfig apiCredentialsConfig;

    private static final String BASE_API_URL  = "https://us-central1-puntored-dev.cloudfunctions.net/technicalTest-developer/api";
    private static final String API_KEY = "mtrQF6Q11eosqyQnkMY0JGFbGqcxVg5icvfVnX1ifIyWDvwGApJ8WUM8nHVrdSkN";

    public AuthToken authenticate(){
        String authenticationURL = BASE_API_URL + "/auth";
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", API_KEY);
        headers.set("Content-Type", "application/json");
        UserCredentials userCredentials = new UserCredentials(apiCredentialsConfig.getUsername(), apiCredentialsConfig.getPassword());
        HttpEntity<UserCredentials> entity = new HttpEntity<>(userCredentials, headers);
        ResponseEntity<AuthToken> response = restTemplate.exchange(authenticationURL, HttpMethod.POST, entity, AuthToken.class);
        return response.getBody();
    }

}
