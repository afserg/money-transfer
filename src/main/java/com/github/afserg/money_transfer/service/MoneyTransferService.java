package com.github.afserg.money_transfer.service;

import com.github.afserg.money_transfer.entity.locker.EntityLock;
import com.github.afserg.money_transfer.entity.locker.EntityLocker;
import com.github.afserg.money_transfer.exception.NegativeOrZeroAmountException;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class MoneyTransferService {
    private final EntityLocker<String> locker = new EntityLocker<>();
    @Inject
    private AccountService accountService;
    @Inject
    private EntityManager em;

    public void transfer(final String from, final String to, final long amount) {
        if (amount <= 0) throw new NegativeOrZeroAmountException();

        EntityLock lockFrom = locker.lock(from);
        EntityLock lockTo = locker.lock(to);

        lockFrom.lock();
        lockTo.lock();
        EntityTransaction et = em.getTransaction();
        try {
            et.begin();
            accountService.decreaseAmount(from, amount);
            accountService.increaseAmount(to, amount);
            et.commit();
        } catch (RuntimeException e) {
            et.rollback();
            throw e;
        } finally {
            lockFrom.unlock();
            lockTo.unlock();
        }
    }

}
