package com.example.account.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/{accountId}", method = RequestMethod.GET)
    public ResponseEntity<Account> findById(@PathVariable("accountId") final Integer accountId) {

        logger.info("AccountController-findById: {}", accountId);
        return new ResponseEntity<>(accountService.findAccountById(accountId), HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<Account>> findByName(@RequestParam(value="name", required=false) String name) {

        logger.info("AccountController-findByName: {}", name);
        List<Account> result = (name == null) ? this.accountService.findAll() :
                this.accountService.findAccountsByFullName(name);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<String> createAccount(@RequestBody Account account,
                                       UriComponentsBuilder builder) {

        logger.info("AccountController-createAccount: {}", account);

        Integer accountId = this.accountService.createAccount(account);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(builder.path("/accounts/{id}").buildAndExpand(accountId).toUri());
        return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    public ResponseEntity<String> updateAccount(@RequestBody Account account,
                                                UriComponentsBuilder builder) {

        logger.info("AccountController-updateAccount: {}", account);

        Integer accountId = this.accountService.updateAccount(account);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(builder.path("/accounts/{id}").buildAndExpand(accountId).toUri());
        return new ResponseEntity<>(responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/{accountId}", method = RequestMethod.DELETE)
    public ResponseEntity<Account> deleteAccount(@PathVariable("accountId") final Integer accountId) {

        logger.info("AccountController-deleteAccount: accountId: {}", accountId);

        Account deletedAccount = this.accountService.deleteAccount(accountId);
        return new ResponseEntity<>(deletedAccount, HttpStatus.OK);
    }
}
