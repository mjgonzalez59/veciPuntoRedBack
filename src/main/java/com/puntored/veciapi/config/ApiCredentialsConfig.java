package com.puntored.veciapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiCredentialsConfig {

    @Value("${puntored.api.credentials.username}")
    private String username;

    @Value("${puntored.api.credentials.password}")
    private String password;

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }
}
