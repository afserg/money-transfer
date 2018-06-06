package com.github.afserg.money_transfer.response;

import com.github.afserg.money_transfer.entity.Account;

public class TransferResponse {
    private Boolean success;
    private String errorMessage;
    private Account accountFrom;
    private Account accountTo;

    private TransferResponse() {
        //private constructor
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Account getAccountFrom() {
        return accountFrom;
    }

    public Account getAccountTo() {
        return accountTo;
    }

    private TransferResponse withSuccess(Boolean success) {
        this.success = success;
        return this;
    }

    private TransferResponse withErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    private TransferResponse withAccountFrom(Account accountFrom) {
        this.accountFrom = accountFrom;
        return this;
    }

    private TransferResponse withAccountTo(Account accountTo) {
        this.accountTo = accountTo;
        return this;
    }

    public static TransferResponse createSuccessResponse(Account accountFrom, Account accountTo) {
        return new TransferResponse().withSuccess(true).withAccountFrom(accountFrom).withAccountTo(accountTo);
    }

    public static TransferResponse createFailResponse(String errorMessage) {
        return new TransferResponse().withSuccess(false).withErrorMessage(errorMessage);
    }
}
