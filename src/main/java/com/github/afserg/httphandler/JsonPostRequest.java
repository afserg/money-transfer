package com.github.afserg.httphandler;

import com.github.afserg.httphandler.exceptions.NotJsonException;
import com.github.afserg.httphandler.exceptions.NotPostRequestException;
import com.github.afserg.ioresponse.IOResponse;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

public class JsonPostRequest implements HttpHandler {
    private static final String CONTENT_TYPE_HEADER = "Content-type";
    private static final String CONTENT_TYPE = "application/json";
    private static final String POST_METHOD_NAME = "POST";
    private final IOResponse response;

    private enum HttpCodes {
        OK(200),
        BAD_REQUEST(400);

        int code;

        HttpCodes(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    public JsonPostRequest(IOResponse response)
    {
        this.response = response;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            checkContentType(httpExchange);
            checkReuestMethod(httpExchange);
            httpExchange.getResponseHeaders().add(CONTENT_TYPE_HEADER, CONTENT_TYPE);
            InputStream requestBody = httpExchange.getRequestBody();
            OutputStream responseBody = httpExchange.getResponseBody();
            httpExchange.sendResponseHeaders(HttpCodes.OK.getCode(), 0);
            response.respond(requestBody, responseBody);
        } catch (Exception e) {
            badResponse(httpExchange, e.getMessage());
        }
    }

    private void checkReuestMethod(final HttpExchange httpExchange) throws NotPostRequestException {
        if (!POST_METHOD_NAME.equals(httpExchange.getRequestMethod())) {
            throw new NotPostRequestException();
        }
    }

    private void checkContentType(final HttpExchange httpExchange) throws NotJsonException {
        Optional.of(httpExchange.getRequestHeaders())
                .map(headers -> headers.getFirst(CONTENT_TYPE_HEADER))
                .map(CONTENT_TYPE::equals)
                .orElseThrow(NotJsonException::new);
    }

    private void badResponse(HttpExchange httpExchange, String message) throws IOException {
        httpExchange.sendResponseHeaders(HttpCodes.BAD_REQUEST.getCode(), 0);
        try (OutputStream responseBody = httpExchange.getResponseBody()) {
            responseBody.write(message.getBytes());
        }
    }
}
