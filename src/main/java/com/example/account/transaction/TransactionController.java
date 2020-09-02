package com.example.account.transaction;

import com.example.account.account.Account;
import com.example.account.account.AccountService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/transactions", method = RequestMethod.GET)
    public ResponseEntity<List<TransactionDTO>> findAll() {
        logger.info("TransactionController-findAll");
        return new ResponseEntity<>(this.transactionService.findAll(), HttpStatus.OK);
    }

    @RequestMapping(value = "/transactions/{transactionId}", method = RequestMethod.GET)
    public ResponseEntity<TransactionDTO> findById(@PathVariable("transactionId") final String transactionId) {

        logger.info("TransactionController-findById: {}", transactionId);
        return new ResponseEntity<>(this.transactionService.findById(transactionId), HttpStatus.OK);
    }

    @RequestMapping(value = "/{accountId}/transactions", method = RequestMethod.GET)
    public ResponseEntity<List<TransactionDTO>> findByAccount(
            @PathVariable("accountId") final Integer accountId) {

        logger.info("TransactionController-findByAccount: accountId: {}", accountId);
        Account account = this.accountService.findAccountById(accountId);
        return new ResponseEntity<>(this.transactionService.findByToAccountOrFromAccount(account, account), HttpStatus.OK);
    }

    @RequestMapping(value = "/transactions", method = RequestMethod.POST)
    public ResponseEntity<List<TransactionDTO>> makeTransaction(
            @RequestBody final TransactionDTO transactionDTO,
            UriComponentsBuilder builder) {

        logger.info("TransactionController-makeTransaction: transaction: {}",
                transactionDTO.toString());
        String transactionId = this.transactionService.makeTransaction(
                transactionDTO.getFromAccountId(), transactionDTO.getToAccountId(),
                transactionDTO.getAmount(), transactionDTO.getDescription());
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(builder.path("/transactions/{id}").
                buildAndExpand(transactionId).toUri());
        return new ResponseEntity<>(responseHeaders, HttpStatus.OK);
    }


}
