package com.github.afserg.money_transfer.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String accountNumber) {
        super("Account with number " + accountNumber + " was not found");
    }
}
