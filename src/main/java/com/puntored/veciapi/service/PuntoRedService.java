package com.puntored.veciapi.service;

import com.puntored.veciapi.entity.Transaction;
import com.puntored.veciapi.entity.User;
import com.puntored.veciapi.model.RechargeReponse;
import com.puntored.veciapi.model.RechargeRequest;
import com.puntored.veciapi.model.Supplier;
import com.puntored.veciapi.repository.TransactionRepository;
import com.puntored.veciapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class PuntoRedService {

    private static final String BASE_API_URL = "https://us-central1-puntored-dev.cloudfunctions.net/technicalTest-developer/api";

    @Autowired
    @Qualifier("interceptedRestTemplate")
    private RestTemplate restTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    public Iterable<Supplier> getSuppliers() {
        String supplierURL = BASE_API_URL + "/getSuppliers";
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Iterable<Supplier>> response = restTemplate.exchange(
                supplierURL, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
                }
        );
        return response.getBody();
    }

    public RechargeReponse makePurchase(RechargeRequest rechargeRequest) {
        String buyURL = BASE_API_URL + "/buy";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<RechargeRequest> entity = new HttpEntity<>(rechargeRequest, headers);
        ResponseEntity<RechargeReponse> response = restTemplate.exchange(buyURL, HttpMethod.POST, entity, RechargeReponse.class);

        //Save in DB the successful transaction
        if (response.getStatusCode().is2xxSuccessful()) {
            RechargeReponse rechargeReponse = response.getBody();
            if (rechargeReponse != null) {

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String username = authentication.getName();
                User user = userRepository.findByUsername(username);

                Transaction transaction = new Transaction(
                        0L,
                        rechargeReponse.cellPhone(),
                        rechargeReponse.value(),
                        rechargeRequest.supplierId(),
                        rechargeReponse.transactionalID(),
                        user
                );
                transactionRepository.save(transaction);
            } else {
                System.err.println("Response parsing failed");
            }
        }
        return response.getBody();
    }
}