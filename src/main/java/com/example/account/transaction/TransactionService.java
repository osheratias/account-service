package com.example.account.transaction;

import com.example.account.account.Account;
import com.example.account.account.AccountService;
import com.example.account.exception.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private static final Logger logger = LogManager.getLogger(TransactionService.class);

    private final ModelMapper modelMapper = new ModelMapper(); // Make it a bean and autowire

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionRepository transactionRepository;

    public List<TransactionDTO> findAll() {
        logger.debug("TransactionService-findAll");
        return Optional.ofNullable(transactionRepository.findAll()).orElse(Collections.emptyList()).
                stream().map(quote -> convertToDto(quote)).collect(Collectors.toList());
    }

    public TransactionDTO findById(String id) {
        logger.debug("TransactionService-findById: id={}", id);
        return transactionRepository.findById(id).map(transaction -> convertToDto(transaction)).orElseThrow(() ->
                new ResourceNotFoundException("Transaction not found:" + id));
    }

    public List<TransactionDTO> findByToAccountOrFromAccount(Account fromAccount, Account toAccount) {
        logger.debug("TransactionService-findByToAccountOrFromAccount: fromAccount={}, toAccount={}",
                fromAccount.toString(), toAccount.toString());
        return transactionRepository.findByToAccountOrFromAccount(fromAccount, toAccount).
                orElse(Collections.emptyList()).stream().map(transaction -> convertToDto(transaction)).collect(Collectors.toList());
    }

    @Transactional
    public String makeTransaction(Integer fromAccountId, Integer toAccountId,
                                    BigDecimal amount, String description) {
        logger.debug("TransactionService-makeTransaction: from: {}, to: {}, amount: {}",
                new Object[] { fromAccountId, toAccountId, amount});

        accountService.withdrawalFunds(fromAccountId, amount);
        accountService.addFunds(toAccountId, amount);

        Transaction transaction = new Transaction();
        transaction.setFromAccount(accountService.findAccountById(fromAccountId));
        transaction.setToAccount(accountService.findAccountById(toAccountId));
        transaction.setAmount(amount);
        transaction.setDescription(description);

        return this.save(transaction).getId();
    }

    @Transactional
    public void deleteTransactionsByAccount(Account account) {
        logger.debug("TransactionService-deleteTransactionsByAccount: account: {}", account);

        transactionRepository.findByToAccountOrFromAccount(account, account).
                orElse(Collections.emptyList()).forEach(transaction ->
                transactionRepository.delete(transaction));
    }

    private TransactionDTO convertToDto(Transaction transaction) {
        return modelMapper.map(transaction, TransactionDTO.class);
    }

    /*
    private Transaction convertToEntity(TransactionDTO transactionDTO) {
        Transaction transaction = modelMapper.map(transactionDTO, Transaction.class);
        transaction.setFromAccount(accountService.
                findAccountById(transactionDTO.getFromAccountId()));
        transaction.setToAccount(accountService.
                findAccountById(transactionDTO.getToAccountId()));

        return transaction;
    }*/

    @Transactional
    private Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

}
