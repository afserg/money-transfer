package com.github.afserg.money_transfer.service;

import com.github.afserg.money_transfer.exception.NegativeOrZeroAmountException;

import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.transaction.UserTransaction;

@Singleton
@TransactionManagement(TransactionManagementType.BEAN)
public class MoneyTransferService {
    @Resource
    private UserTransaction utx;
    @Inject
    private AccountService accountService;

    public void transfer(final String from, final String to, final long amount) throws Exception {
        if (amount <= 0) throw new NegativeOrZeroAmountException();

        try {
            utx.begin();
            accountService.decreaseAmount(from, amount);
            accountService.increaseAmount(to, amount);
            utx.commit();

        } catch (Exception ex) {
            utx.rollback();
            throw ex;
        }
    }

}
