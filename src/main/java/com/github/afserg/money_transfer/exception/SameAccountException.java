package com.github.afserg.money_transfer.exception;

public class SameAccountException extends RuntimeException {
    public SameAccountException() {
        super("Account \"from\" must be different from the account \"to\"");
    }
}
