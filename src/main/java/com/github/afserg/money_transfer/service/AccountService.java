package com.github.afserg.money_transfer.service;

import com.github.afserg.money_transfer.entity.Account;
import com.github.afserg.money_transfer.exception.AccountNotFoundException;
import com.github.afserg.money_transfer.exception.InsufficientFundsException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;

public class AccountService {
    @Inject
    private EntityManager em;

    @PostConstruct
    private void createAccounts() {
        //test accounts
        em.getTransaction().begin();
        createAccount("123", 1000L);
        createAccount("234", 1000L);
        em.getTransaction().commit();
    }

    public Account findAccount(final String number) throws AccountNotFoundException {
        String query = "SELECT a from Account a where a.number = :number";
        return em.createQuery(query, Account.class).setParameter("number", number).getResultStream()
                .findFirst().orElseThrow(() -> new AccountNotFoundException(number));
    }

    void increaseAmount(final String number, final long increase) {
        Account account = findAccount(number);
        account.setAmount(account.getAmount() + increase);
    }

    void decreaseAmount(final String number, final long decrease) {
        Account account = findAccount(number);
        if (account.getAmount() >= decrease) {
            account.setAmount(account.getAmount() - decrease);
        } else {
            throw new InsufficientFundsException(number);
        }
    }

    private void createAccount(String number, long amount) {
        Account account = new Account();
        account.setNumber(number);
        account.setAmount(amount);
        em.persist(account);
    }
}
