package com.github.afserg.moneytransfer.entities;

public class SuccessfulResponse implements Response {
    private class Data {
        private String transferId;

        String getTransferId() {
            return transferId;
        }
    }

    private String code;
    private String status;
    private String message;
    private Data data;

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
        return data.getTransferId();
    }
}
