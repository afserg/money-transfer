package com.github.afserg.httphandler.exceptions;

public class NotPostRequestException extends Exception {
    public NotPostRequestException() {
        super("Only POST request methods are allowed");
    }
}
