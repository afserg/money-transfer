package com.github.afserg.ioresponse;

import com.github.afserg.account.Account;
import com.github.afserg.account.Transfer;
import com.github.afserg.entitylocker.EntityLocker;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.*;

public class JsonMoneyTransferResponse implements IOResponse {
    private static final String FROM = "from";
    private static final String TO = "to";
    private static final String AMOUNT = "amount";
    private final EntityLocker<String> locker;
    private final String connectionString;
    private String from;
    private String to;
    private Long amount;

    public enum ResponseCodes {
        CREATED(201, "success", ""),
        ERROR(500, "fail", "could not transfer");

        int code;
        String status;
        String message;

        ResponseCodes(int code, String status, String message) {
            this.code = code;
            this.status = status;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }

    private enum ResponseFields {
        code,
        status,
        message,
        data,
        transferId
    }

    public JsonMoneyTransferResponse(EntityLocker<String> locker, String connectionString) {
        this.locker = locker;
        this.connectionString = connectionString;
    }

    @Override
    public void respond(final InputStream is, final OutputStream os) throws IOException {

        try(JsonWriter jw = new JsonWriter(new OutputStreamWriter(os))) {
            try {
                parseInputStream(is);
                Transfer transfer = new Transfer(new Account(from), new Account(to), amount);
                Long transferId = transfer.commit(connectionString, locker);
                respond(jw, transferId);
            } catch (Exception ex) {
                respond(jw, ex);
            }
        }
    }

    private void parseInputStream(final InputStream is) throws Exception {
        try (JsonReader jr = new JsonReader(new InputStreamReader(is))) {
            jr.beginObject();
            while (jr.hasNext()) {
                switch (jr.nextName()) {
                    case FROM:
                        from = jr.nextString();
                        break;
                    case TO:
                        to = jr.nextString();
                        break;
                    case AMOUNT:
                        amount = jr.nextLong();
                        break;
                    default:
                        jr.skipValue();
                }
            }
            jr.endObject();
        }
    }

    private void respond(final JsonWriter jw, final long transferId) throws IOException {
        jw.beginObject();
        createResponse(jw, ResponseCodes.CREATED);
        jw.name(ResponseFields.data.name())
                .beginObject()
                .name(ResponseFields.transferId.name())
                .value(transferId)
                .endObject();
        jw.endObject();
    }

    private void respond(final JsonWriter jw, final Exception ex) throws IOException {
        jw.beginObject();
        createResponse(jw, ResponseCodes.ERROR);
        jw.name(ResponseFields.data.name()).value(ex.getMessage());
        jw.endObject();
    }

    private void createResponse(final JsonWriter jw, final ResponseCodes code) throws IOException {
        jw.name(ResponseFields.code.name()).value(code.getCode())
                .name(ResponseFields.status.name()).value(code.getStatus())
                .name(ResponseFields.message.name()).value(code.getMessage());
    }
}
