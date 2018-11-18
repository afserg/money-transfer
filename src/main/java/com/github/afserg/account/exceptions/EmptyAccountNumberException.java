package com.github.afserg.account.exceptions;

public class EmptyAccountNumberException extends RuntimeException {
    public EmptyAccountNumberException() {
        super("Account number is empty");
    }
}
