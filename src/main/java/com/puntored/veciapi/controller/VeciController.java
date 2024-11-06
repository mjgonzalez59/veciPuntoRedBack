package com.puntored.veciapi.controller;

import com.puntored.veciapi.entity.Transaction;
import com.puntored.veciapi.entity.User;
import com.puntored.veciapi.model.RechargeReponse;
import com.puntored.veciapi.model.RechargeRequest;
import com.puntored.veciapi.model.Supplier;
import com.puntored.veciapi.repository.TransactionRepository;
import com.puntored.veciapi.repository.UserRepository;
import com.puntored.veciapi.service.PuntoRedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

//@CrossOrigin()
@RestController
@RequestMapping("/api")
public class VeciController {

    @Autowired
    private PuntoRedService puntoRedService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping("/login")
    public ResponseEntity<Void> login() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/suppliers")
    public ResponseEntity<Iterable<Supplier>> getSuppliers() {
        Iterable<Supplier> response = puntoRedService.getSuppliers();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/recharge")
    public ResponseEntity<RechargeReponse> recharge(@RequestBody RechargeRequest rechargeRequest) {
        RechargeReponse response = puntoRedService.makePurchase(rechargeRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transactions")
    public ResponseEntity<Iterable<Transaction>> getTransactions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        Iterable<Transaction> response = transactionRepository.findAllByUser(user);
        response.forEach(transaction -> {
            transaction.getUser().setPassword(null);
        });
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transaction")
    public ResponseEntity<Transaction> getTransaction(@RequestParam String transactionalId) {
        Transaction response = transactionRepository.findByTransactionalId(transactionalId);
        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
