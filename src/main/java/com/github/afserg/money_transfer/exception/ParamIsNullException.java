package com.github.afserg.money_transfer.exception;

public class ParamIsNullException extends RuntimeException {
    public ParamIsNullException(String paramName) {
        super(paramName + " not specified");
    }
}
