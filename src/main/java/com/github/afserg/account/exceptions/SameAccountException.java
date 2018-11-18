package com.github.afserg.account.exceptions;

public class SameAccountException extends RuntimeException {
    public SameAccountException() {
        super("Account \"from\" must be different from the account \"to\"");
    }
}
