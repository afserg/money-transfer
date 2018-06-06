package com.github.afserg.money_transfer.request;

public class TransferRequest {
    private String from;
    private String to;
    private Long amount;

    private TransferRequest() {
        //private constructor
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Long getAmount() {
        return amount;
    }
}
