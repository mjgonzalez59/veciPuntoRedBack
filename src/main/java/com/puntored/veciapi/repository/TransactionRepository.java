package com.puntored.veciapi.repository;

import com.puntored.veciapi.entity.Transaction;
import com.puntored.veciapi.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    Iterable<Transaction> findAllByUser(User user);

    Transaction findByTransactionalId(String transactionalId);
}