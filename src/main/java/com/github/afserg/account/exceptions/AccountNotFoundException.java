package com.github.afserg.account.exceptions;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String accountNumber) {
        super("Account with number " + accountNumber + " was not found");
    }
}
