package com.github.afserg.account;

import com.github.afserg.account.exceptions.AccountNotFoundException;
import com.github.afserg.account.exceptions.EmptyAccountNumberException;
import com.github.afserg.account.exceptions.InsufficientFundsException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Account {
    private static final String QUERY_BALANCE = "SELECT balance FROM accounts WHERE number = ?";
    private static final String UPDATE_BALANCE = "UPDATE accounts SET balance = ? where number = ?";
    private String number;

    public Account(final String number) {
        if (number == null || number.trim().isEmpty()) throw new EmptyAccountNumberException();
        this.number = number;
    }

    String getNumber() {
        return number;
    }

    public Long getBalance(final Connection cnn) throws SQLException {
        try (PreparedStatement stmt = cnn.prepareStatement(QUERY_BALANCE)) {
            stmt.setString(1, number);
            ResultSet rs = stmt.executeQuery();
            if (rs.first()) {
                return rs.getLong(1);
            } else {
                throw new AccountNotFoundException(number);
            }
        }
    }

    public int updateBalance(final Long balance, final Connection cnn) throws SQLException {
        try (PreparedStatement stmt = cnn.prepareStatement(UPDATE_BALANCE)) {
            stmt.setLong(1, balance);
            stmt.setString(2, number);
            return stmt.executeUpdate();
        }
    }

    void decreaseBalance(final Long amount, final Connection cnn) throws SQLException {
        Long balance = getBalance(cnn);
        if (balance < amount) {
            throw new InsufficientFundsException(number);
        }
        updateBalance(balance - amount, cnn);
    }

    void increaseBalance(final Long amount, final Connection cnn) throws SQLException {
        Long balance = getBalance(cnn);
        updateBalance(balance + amount, cnn);
    }
}
