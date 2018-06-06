package com.github.afserg.money_transfer;

import com.github.afserg.money_transfer.entity.Account;
import com.github.afserg.money_transfer.exception.ParamIsNullException;
import com.github.afserg.money_transfer.exception.SameAccountException;
import com.github.afserg.money_transfer.request.TransferRequest;
import com.github.afserg.money_transfer.response.TransferResponse;
import com.github.afserg.money_transfer.service.AccountService;
import com.github.afserg.money_transfer.service.MoneyTransferService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("money/transfer")
public class MoneyTransfer {
    @Inject
    private MoneyTransferService moneyTransferService;
    @Inject
    private AccountService accountService;

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public TransferResponse transfer(final TransferRequest request) {

        TransferResponse response;
        try {
            checkRequest(request);
            moneyTransferService.transfer(request.getFrom(), request.getTo(), request.getAmount());
            Account accountFrom = accountService.findAccount(request.getFrom());
            Account accountTo = accountService.findAccount(request.getTo());
            response = TransferResponse.createSuccessResponse(accountFrom, accountTo);
        } catch (Exception ex) {
            response = TransferResponse.createFailResponse(ex.getMessage());
        }
        return response;
    }

    private void checkRequest(final TransferRequest request) {
        if (empty(request.getFrom())) throw new ParamIsNullException("from");
        if (empty(request.getTo())) throw new ParamIsNullException("to");
        if (request.getAmount() == null) throw new ParamIsNullException("amount");
        if (request.getFrom().equals(request.getTo())) throw new SameAccountException();
    }

    private boolean empty( final String s ) {
        // Null-safe, short-circuit evaluation.
        return s == null || s.trim().isEmpty();
    }
}
