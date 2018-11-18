package com.github.afserg.account.exceptions;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String accountNumber) {
        super("Account with number " + accountNumber + " has insufficient funds");
    }
}
