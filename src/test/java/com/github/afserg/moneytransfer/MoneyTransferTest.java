package com.github.afserg.moneytransfer;


import com.github.afserg.account.Account;
import com.github.afserg.account.exceptions.*;
import com.github.afserg.moneytransfer.entities.FailedResponse;
import com.github.afserg.moneytransfer.entities.Response;
import com.github.afserg.moneytransfer.entities.SuccessfulResponse;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

import static com.github.afserg.ioresponse.JsonMoneyTransferResponse.ResponseCodes.CREATED;
import static com.github.afserg.ioresponse.JsonMoneyTransferResponse.ResponseCodes.ERROR;
import static org.junit.jupiter.api.TestInstance.Lifecycle;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(Lifecycle.PER_CLASS)
class MoneyTransferTest {
    private static final String SERVICE_PATH = "http://127.0.0.1:8081/money/transfers";
    private static final String DB_CONN_STRING = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static final Gson GSON = new Gson();
    private static final String ACC_FROM_NUMBER = "123";
    private static final String ACC_TO_NUMBER = "234";
    private static final Account ACC_FROM = new Account(ACC_FROM_NUMBER);
    private static final Account ACC_TO = new Account(ACC_TO_NUMBER);
    private static final long INITIAL_BALANCE = 1000L;
    private static final long TEST_AMOUNT = 1L;
    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    private class Request {
        private final String from;
        private final String to;
        private final long amount;

        Request(final String from, final String to, final long amount) {
            this.from = from;
            this.to = to;
            this.amount = amount;
        }

        Response process(final Class<? extends Response> responseClass) {
            Response response;
            try {
                HttpPost httpPost = new HttpPost(SERVICE_PATH);
                httpPost.setEntity(new StringEntity(GSON.toJson(this)));
                try (CloseableHttpResponse httpResponse = httpClient.execute(httpPost)) {
                    HttpEntity httpEntity = httpResponse.getEntity();
                    try (JsonReader jr = new JsonReader(new InputStreamReader(httpEntity.getContent()))) {
                        response = new Gson().fromJson(jr, responseClass);
                    }
                    EntityUtils.consume(httpEntity);
                }
            } catch (IOException e) {
                response = new FailedResponse();
            }
            return response;
        }
    }

    @BeforeAll
    void runMoneyTransfer() throws IOException, SQLException {
        MoneyTransfer.main(null);
    }

    @BeforeEach
    void setInitialBalance() throws SQLException {
        try (Connection cnn = DriverManager.getConnection(DB_CONN_STRING)) {
            ACC_FROM.updateBalance(INITIAL_BALANCE, cnn);
            ACC_TO.updateBalance(INITIAL_BALANCE, cnn);
        }
    }

    @Test
    void transfer() throws SQLException {
        Request request = new Request(ACC_FROM_NUMBER, ACC_TO_NUMBER, TEST_AMOUNT);
        Response response = request.process(SuccessfulResponse.class);
        response.assertStatus(CREATED);
        try (Connection cnn = DriverManager.getConnection(DB_CONN_STRING)) {
            assertEquals((Long) (INITIAL_BALANCE - TEST_AMOUNT), ACC_FROM.getBalance(cnn));
            assertEquals((Long) (INITIAL_BALANCE + TEST_AMOUNT), ACC_TO.getBalance(cnn));
        }
    }

    @Test
    void insufficientFunds() {
        long largeAmount = 10000L;
        Request request = new Request(ACC_FROM_NUMBER, ACC_TO_NUMBER, largeAmount);
        Response response = request.process(FailedResponse.class);
        response.assertStatus(ERROR);
        assertEquals(new InsufficientFundsException(ACC_FROM_NUMBER).getMessage(), response.getData());
    }

    @Test
    void accountNotFound() {
        String fakeAccNum = "345";
        Request request = new Request(fakeAccNum, ACC_TO_NUMBER, TEST_AMOUNT);
        Response response = request.process(FailedResponse.class);
        response.assertStatus(ERROR);
        assertEquals(new AccountNotFoundException(fakeAccNum).getMessage(), response.getData());
    }

    @Test
    void negativeOrZeroAmount() {
        long negativeAmount = -10L;
        Request request = new Request(ACC_FROM_NUMBER, ACC_TO_NUMBER, negativeAmount);
        Response response = request.process(FailedResponse.class);
        response.assertStatus(ERROR);
        assertEquals(new WrongAmountException().getMessage(), response.getData());
    }

    @Test
    void sameAccount() {
        Request request = new Request(ACC_FROM_NUMBER, ACC_FROM_NUMBER, TEST_AMOUNT);
        Response response = request.process(FailedResponse.class);
        response.assertStatus(ERROR);
        assertEquals(new SameAccountException().getMessage(), response.getData());
    }

    @Test
    void paramIsNull() {
        Request request = new Request("", ACC_FROM_NUMBER, TEST_AMOUNT);
        Response response = request.process(FailedResponse.class);
        response.assertStatus(ERROR);
        assertEquals(new EmptyAccountNumberException().getMessage(), response.getData());
    }

    @Test
    void concurrentTransfer() throws InterruptedException, SQLException {
        CountDownLatch transfers = new CountDownLatch(100);
        Request request = new Request(ACC_FROM_NUMBER, ACC_TO_NUMBER, TEST_AMOUNT);
        Request reversedRequest = new Request(ACC_TO_NUMBER, ACC_FROM_NUMBER, TEST_AMOUNT);
        for (int i = 0; i < 50; i++) {
            new Thread(() -> {
                request.process(SuccessfulResponse.class);
                transfers.countDown();
            }).start();
        }
        for (int i = 0; i < 50; i++) {
            new Thread(() -> {
                reversedRequest.process(SuccessfulResponse.class);
                transfers.countDown();
            }).start();
        }
        transfers.await();
        try (Connection cnn = DriverManager.getConnection(DB_CONN_STRING)) {
            assertEquals((Long) INITIAL_BALANCE, ACC_FROM.getBalance(cnn));
            assertEquals((Long) INITIAL_BALANCE, ACC_TO.getBalance(cnn));
        }
    }
}
