package com.github.afserg.money_transfer.exception;

public class NegativeOrZeroAmountException extends RuntimeException {
    public NegativeOrZeroAmountException() {
        super("Amount must be greater than 0");
    }
}
