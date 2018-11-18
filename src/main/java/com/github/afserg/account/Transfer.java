package com.github.afserg.account;

import com.github.afserg.account.exceptions.SameAccountException;
import com.github.afserg.account.exceptions.WrongAmountException;
import com.github.afserg.entitylocker.EntityLocker;

import java.sql.*;
import java.util.concurrent.locks.Lock;

public class Transfer {
    private static final String INSERT_TRANSFER = "INSERT INTO transfers (accFrom, accTo, amount) VALUES (?, ?, ?)";

    private final Account from;
    private final Account to;
    private final Long amount;

    public Transfer(final Account from, final Account to, final Long amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public Long commit(String connectionString, EntityLocker<String> locker) throws SQLException {
        if (amount == null || amount <= 0) throw new WrongAmountException();
        if (from.getNumber().equals(to.getNumber())) throw new SameAccountException();

        //Please note that there is no need to sort locks cause EntityLocker avoids deadlock
        Lock lockFrom = locker.lock(from.getNumber());
        Lock lockTo = locker.lock(to.getNumber());

        lockFrom.lock();
        lockTo.lock();

        try (Connection cnn = DriverManager.getConnection(connectionString)) {
            try {
                cnn.setAutoCommit(false);
                from.decreaseBalance(amount, cnn);
                to.increaseBalance(amount, cnn);
                Long id = insert(cnn);
                cnn.commit();
                return id;
            } catch (SQLException e) {
                cnn.rollback();
                throw e;
            }
        } finally {
            lockFrom.unlock();
            lockTo.unlock();
        }
    }

    private Long insert(Connection cnn) throws SQLException {
        try (PreparedStatement stmt = cnn.prepareStatement(INSERT_TRANSFER, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, from.getNumber());
            stmt.setString(2, to.getNumber());
            stmt.setLong(3, amount);
            int affectedRowsCount = stmt.executeUpdate();
            if (affectedRowsCount == 0) {
                throw new SQLException("Creating transfer failed, no rows affected.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Creating transfer failed, no ID obtained.");
                }
            }
        }
    }
}
