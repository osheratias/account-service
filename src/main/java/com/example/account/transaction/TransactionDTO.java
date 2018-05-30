package com.example.account.transaction;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TransactionDTO {

    private String id;
    private BigDecimal amount;
    private Integer fromAccountId;
    private Integer toAccountId;
    private Date date;
    private String description;
}
