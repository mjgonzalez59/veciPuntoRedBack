package com.puntored.veciapi.config.interceptor;

import com.puntored.veciapi.service.PuntoRedAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

@Component
public class AuthInterceptor implements ClientHttpRequestInterceptor {

    @Autowired
    private PuntoRedAuthenticationService authenticationService;

    private Instant tokenExpiration;

    private String authToken;

    @Override
    @NonNull
    public ClientHttpResponse intercept(@NonNull HttpRequest request, @NonNull byte[] body, @NonNull ClientHttpRequestExecution execution) throws IOException {
        if (authToken == null || Instant.now().isAfter(tokenExpiration)) {
            authToken = authenticationService.authenticate().token();
            tokenExpiration = Instant.now().plusSeconds(3600);
        }
        HttpHeaders headers = request.getHeaders();
        if (!headers.containsKey(HttpHeaders.AUTHORIZATION) && authToken != null) {
            headers.set(HttpHeaders.AUTHORIZATION, authToken);
        }
        return execution.execute(request, body);
    }
}