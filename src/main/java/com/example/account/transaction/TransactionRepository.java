package com.example.account.transaction;

import com.example.account.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    Optional<Transaction> findById(String id);
    Optional<List<Transaction>> findByToAccountOrFromAccount(Account fromAccount, Account toAccount);
}
