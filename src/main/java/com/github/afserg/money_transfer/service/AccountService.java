package com.github.afserg.money_transfer.service;

import com.github.afserg.money_transfer.entity.Account;
import com.github.afserg.money_transfer.exception.AccountNotFoundException;
import com.github.afserg.money_transfer.exception.InsufficientFundsException;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Singleton
public class AccountService {
    @PersistenceContext(unitName = "money-transfer")
    private EntityManager em;

    @PostConstruct
    private void createAccounts() {
        //test accounts
        createAccount("123", 1000L);
        createAccount("234", 1000L);
    }

    public Account findAccount(final String number) throws AccountNotFoundException {
        return Optional.ofNullable(em.find(Account.class, number))
                .orElseThrow(() -> new AccountNotFoundException(number));
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
