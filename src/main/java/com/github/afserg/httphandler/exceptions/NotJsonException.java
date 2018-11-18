package com.github.afserg.httphandler.exceptions;

public class NotJsonException extends Exception {
    public NotJsonException() {
        super("Only JSON content type are allowed");
    }
}
