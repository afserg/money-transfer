package com.github.afserg.account.exceptions;

public class WrongAmountException extends RuntimeException {
    public WrongAmountException() {
        super("Amount must be greater than 0");
    }
}
