package com.github.afserg.money_transfer.exception;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String accountNumber) {
        super("Account with number " + accountNumber + " has insufficient funds");
    }
}
