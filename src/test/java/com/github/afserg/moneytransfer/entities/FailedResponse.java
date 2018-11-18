package com.github.afserg.moneytransfer.entities;

public class FailedResponse implements Response {
    private String code;
    private String status;
    private String message;
    private String data;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getData() {
        return data;
    }
}
