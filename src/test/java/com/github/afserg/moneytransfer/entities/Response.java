package com.github.afserg.moneytransfer.entities;

import com.github.afserg.ioresponse.JsonMoneyTransferResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public interface Response {
    String getCode();
    String getStatus();
    String getMessage();
    String getData();

    default void assertStatus(JsonMoneyTransferResponse.ResponseCodes code) {
        assertEquals(String.valueOf(code.getCode()), getCode());
        assertEquals(code.getStatus(), getStatus());
        assertEquals(code.getMessage(), getMessage());
    }
}
