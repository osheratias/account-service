package com.example.account.account;

import lombok.Data;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.persistence.GeneratedValue;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "id")
    private Integer id;

    @Column(name = "user_id", nullable = false, length = 250)
    private String userId;

    @Column(name = "full_name", nullable = false, length = 250)
    private String fullName;

    @Column(name = "creation_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    private Date creationDate;

    @Column(name = "balance", precision = 14, scale = 2, nullable=false)
    private BigDecimal balance = new BigDecimal(0);

    @Column(name = "address", nullable = false, length = 250)
    private String address;

    @Column(name = "email", length = 250)
    private String email;

    @PrePersist
    private void creationdate() {
        this.creationDate = new Date();
    }

    private synchronized void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void addFunds(BigDecimal amount) {
        this.setBalance(this.getBalance().add(amount));
    }

    public void withdrawalFunds(BigDecimal amount) {
        this.setBalance(this.getBalance().subtract(amount));
    }
}
