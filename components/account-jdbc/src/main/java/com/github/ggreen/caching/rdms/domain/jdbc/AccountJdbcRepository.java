package com.github.ggreen.caching.rdms.domain.jdbc;

import com.github.ggreen.caching.rdms.domain.Account;
import com.github.ggreen.caching.rdms.domain.AccountRepository;
import nyla.solutions.core.exception.DataException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Supplier;

public class AccountJdbcRepository implements AccountRepository
{
    private final Supplier<Connection> supplier;

    public AccountJdbcRepository(Supplier<Connection> supplier)
    {
        this.supplier = supplier;
    }

    public Account create(Account account)
    {
        String insertSql = "INSERT INTO app.account(ACCOUNT_ID, ACCOUNT_NM) values(?,?)";
        try(Connection connection = this.supplier.get())
        {

            try(PreparedStatement preparedStatement = connection.prepareStatement(insertSql))
            {
                preparedStatement.setLong(1,account.getId());
                preparedStatement.setString(2,account.getName());

                preparedStatement.execute();
            }

            return account;
        }
        catch (SQLException e) {
            throw new DataException(e);
        }
    }

    public Account findById(Long accountId)
    {
        String sqlText = "select ACCOUNT_ID,ACCOUNT_NM from app.account  where ACCOUNT_ID = ?";

        try(Connection connection = supplier.get())
        {
            try(PreparedStatement statement = connection.prepareStatement(sqlText) )
            {
                statement.setLong(1,accountId);
                try(ResultSet resultSet = statement.executeQuery())
                {
                    if(!resultSet.next())
                        return null;

                    return   Account.builder().id(resultSet.getLong(1))
                                    .name(resultSet.getString(1))
                                    .build();

                }
            }

        }
        catch (SQLException e) {
            throw new DataException(e);
        }
    }



    public Account update(Account account)
    {
        String insertSql = "UPDATE  app.account set ACCOUNT_NM = ? where ACCOUNT_ID = ?";
        try(Connection connection = this.supplier.get())
        {

            try(PreparedStatement preparedStatement = connection.prepareStatement(insertSql))
            {
                preparedStatement.setString(1,account.getName());
                preparedStatement.setLong(2,account.getId());

                preparedStatement.execute();
            }
            return account;
        }
        catch (SQLException e) {
            throw new DataException(e);
        }
    }

    public boolean deleteAccountById(Long accountId)
    {
        String insertSql = "DELETE from  app.account where ACCOUNT_ID = ?";
        try(Connection connection = this.supplier.get())
        {
            try(PreparedStatement preparedStatement = connection.prepareStatement(insertSql))
            {
                preparedStatement.setLong(1,accountId);

                return preparedStatement.executeUpdate() > 0;
            }
        }
        catch (SQLException e) {
            throw new DataException(e);
        }
    }
}
