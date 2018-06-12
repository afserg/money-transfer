package com.github.afserg.money_transfer;

import com.github.afserg.money_transfer.response.TransferResponse;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static com.github.afserg.money_transfer.MoneyTransfer.BASE_URI;
import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.*;

public class MoneyTransferTest {
    private static final String REQUEST_PATH = BASE_URI + "money/transfer/";
    private static HttpServer server;

    @BeforeClass
    public static void startServer() {
        server = MoneyTransfer.startServer();
    }

    @AfterClass
    public static void serverShutdown() {
        server.shutdown();
    }

    @Test
    public void transfer() {
        String requestBody = getRequestBody("123", "234", 100L);
        TransferResponse transferResponse = getTransferResponse(getRequestSpec(requestBody));
        assertTrue(transferResponse.getSuccess());
        assertNull(transferResponse.getErrorMessage());
        assertEquals(900L, transferResponse.getAccountFrom().getAmount());
        assertEquals(1100L, transferResponse.getAccountTo().getAmount());
    }

    @Test
    public void insufficientFunds() {
        String requestBody = getRequestBody("123", "234", 10000L);
        TransferResponse transferResponse = getTransferResponse(getRequestSpec(requestBody));
        assertFalse(transferResponse.getSuccess());
        assertEquals("Account with number 123 has insufficient funds", transferResponse.getErrorMessage());
    }

    @Test
    public void accountNotFound() {
        String requestBody = getRequestBody("345", "234", 100L);
        TransferResponse transferResponse = getTransferResponse(getRequestSpec(requestBody));
        assertFalse(transferResponse.getSuccess());
        assertEquals("Account with number 345 was not found", transferResponse.getErrorMessage());
    }

    @Test
    public void negativeOrZeroAmount() {
        String requestBody = getRequestBody("123", "234", -100L);
        TransferResponse transferResponse = getTransferResponse(getRequestSpec(requestBody));
        assertFalse(transferResponse.getSuccess());
        assertEquals("Amount must be greater than 0", transferResponse.getErrorMessage());
    }

    @Test
    public void sameAccount() {
        String requestBody = getRequestBody("123", "123", 100L);
        TransferResponse transferResponse = getTransferResponse(getRequestSpec(requestBody));
        assertFalse(transferResponse.getSuccess());
        assertEquals("Account \"from\" must be different from the account \"to\"",
                transferResponse.getErrorMessage());
    }

    @Test
    public void paramIsNull() {
        String requestBody = getRequestBody("", "234", 100L);
        TransferResponse transferResponse = getTransferResponse(getRequestSpec(requestBody));
        assertFalse(transferResponse.getSuccess());
        assertEquals("from is not specified", transferResponse.getErrorMessage());
    }

    @Test
    public void concurrentTransfer() throws InterruptedException {
        CountDownLatch transfers = new CountDownLatch(99);
        String requestBody = getRequestBody("123", "234", 1L);
        String requestBody2 = getRequestBody("234", "123", 1L);
        for (int i = 0; i < 49; i++) {
            new Thread(() -> {
                getTransferResponse(getRequestSpec(requestBody));
                transfers.countDown();
            }).start();
        }
        for (int i = 0; i < 50; i++) {
            new Thread(() -> {
                getTransferResponse(getRequestSpec(requestBody2));
                transfers.countDown();
            }).start();
        }
        transfers.await();
        TransferResponse transferResponse = getTransferResponse(getRequestSpec(requestBody));
        assertTrue(transferResponse.getSuccess());
        assertNull(transferResponse.getErrorMessage());
        assertEquals(1000L, transferResponse.getAccountFrom().getAmount());
        assertEquals(1000L, transferResponse.getAccountTo().getAmount());
    }

    private String getRequestBody(String from, String to, long amount) {
        return "{\"from\":\"" + from + "\",\"to\":\"" + to + "\",\"amount\":\"" + amount + "\"}";
    }

    private RequestSpecification getRequestSpec(String body) {
        return new RequestSpecBuilder()
                .setBody(body)
                .setContentType("application/json; charset=UTF-8")
                .build();
    }

    private TransferResponse getTransferResponse(RequestSpecification requestSpec) {
        Response response = given().spec(requestSpec).when().post(REQUEST_PATH);
        assertEquals("Status check failed!", 200, response.getStatusCode());
        assertEquals("Wrong response content type!", "application/json", response.getContentType());
        assertNotNull("Response body is Null!", response.getBody());
        TransferResponse transferResponse = response.getBody().as(TransferResponse.class);
        assertNotNull("Response is empty!", transferResponse);
        return transferResponse;
    }
}
