package com.github.afserg.money_transfer.service;

import com.github.afserg.money_transfer.entity.locker.EntityLock;
import com.github.afserg.money_transfer.entity.locker.EntityLocker;
import com.github.afserg.money_transfer.exception.NegativeOrZeroAmountException;

import javax.inject.Inject;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MoneyTransferService {
    //private UserTransaction utx;
    private final EntityLocker<String> locker = new EntityLocker<>();
    @Inject
    private AccountService accountService;

    public void transfer(final String from, final String to, final long amount) {
        if (amount <= 0) throw new NegativeOrZeroAmountException();

        EntityLock lockFrom = locker.lock(from);
        EntityLock lockTo = locker.lock(to);

        lockFrom.lock();
        lockTo.lock();
        try {
            accountService.decreaseAmount(from, amount);
            accountService.increaseAmount(to, amount);
        } finally {
            lockFrom.unlock();
            lockTo.unlock();
        }
    }

}
