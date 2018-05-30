package com.example.account.account;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findById(Integer id);
    Optional<List<Account>> findByFullNameIgnoreCaseContaining(String name);
}

