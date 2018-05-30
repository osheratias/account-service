package com.example.account.account;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.example.account.exception.ResourceNotFoundException;

import com.example.account.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionService transactionService;

    public List<Account> findAll() {
        logger.debug("AccountService-findAll");
        return Optional.ofNullable(accountRepository.findAll()).orElse(Collections.emptyList());
    }

    public Account findAccountById(Integer accountId) {
        logger.debug("AccountService-findById: id={}", accountId);
        return this.accountRepository.findById(accountId).orElseThrow(() ->
                new ResourceNotFoundException("Account not found:" + accountId));
    }

    public List<Account> findAccountsByFullName(String fullName) {
        logger.debug("AccountService-findAccountsByFullName: fullname={}", fullName);
        return this.accountRepository.findByFullNameIgnoreCaseContaining(fullName).orElse(Collections.emptyList());
    }

    @Transactional
    public Integer createAccount(Account account) {
        logger.debug("AccountService-createAccount: account: {}", account.toString());
        Account newAccount = new Account();
        newAccount.setUserId(account.getUserId());
        this.updateAccountProperties(newAccount, account);
        return this.save(newAccount).getId();
    }

    @Transactional
    public Integer updateAccount(Account account) {
        logger.debug("AccountService-updateAccount: account: {}", account.toString());
        Account toUpdate = this.findAccountById(account.getId());
        this.updateAccountProperties(toUpdate, account);
        return this.save(toUpdate).getId();
    }

    @Transactional
    public Account deleteAccount(Integer accountId) {
        logger.debug("AccountService-deleteAccount: accountId: {}", accountId);
        Account toDelete = this.findAccountById(accountId);
        transactionService.deleteTransactionsByAccount(toDelete);
        return this.delete(toDelete);
    }

    @Transactional
    public void addFunds(Integer accountId, BigDecimal amount) {
        logger.debug("AccountService-addFunds: accountId: {}, amount: {}", accountId, amount);
        Account account = this.findAccountById(accountId);
        account.addFunds(amount);
        this.save(account);
    }

    @Transactional
    public void withdrawalFunds(Integer accountId, BigDecimal amount) {
        logger.debug("AccountService-withdrawalFunds: account: {}, amount: {}", accountId, amount);
        Account account = this.findAccountById(accountId);
        account.withdrawalFunds(amount);
        this.save(account);
    }

    private void updateAccountProperties(Account toUpdate, Account newData) {
        // Only update allowed properties
        toUpdate.setUserId(newData.getUserId());
        toUpdate.setFullName(newData.getFullName());
        toUpdate.setEmail(newData.getEmail());
        toUpdate.setAddress(newData.getAddress());
    }

    @Transactional
    private Account save(Account account) {
        return accountRepository.save(account);
    }

    @Transactional
    private Account delete(Account account) {
        accountRepository.delete(account);
        return account;
    }
}
