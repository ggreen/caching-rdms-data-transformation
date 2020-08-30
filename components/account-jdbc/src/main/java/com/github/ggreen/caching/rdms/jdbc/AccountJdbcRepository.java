package com.github.ggreen.caching.rdms.jdbc;

import com.github.ggreen.caching.rdms.domain.Account;
import com.github.ggreen.caching.rdms.domain.AccountRepository;
import nyla.solutions.core.exception.DataException;
import nyla.solutions.core.util.Debugger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Supplier;

/**
 * @author Gregory Green
 */
public class AccountJdbcRepository implements AccountRepository
{
    private final Supplier<Connection> supplier;

    public AccountJdbcRepository()
    {
        this(new ApacheDbcpConnections());
    }
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

                Debugger.println(this,insertSql);
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
                Debugger.println(this,sqlText);
                try(ResultSet resultSet = statement.executeQuery())
                {
                    if(!resultSet.next())
                        return null;

                    return   Account.builder().id(resultSet.getLong(1))
                                    .name(resultSet.getString(2))
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
        executeUpdate(account);

        return account;
    }

    private int executeUpdate(Account account)
    {
        String updateSql = "UPDATE  app.account set ACCOUNT_NM = ? where ACCOUNT_ID = ?";
        try(Connection connection = this.supplier.get())
        {

            try(PreparedStatement preparedStatement = connection.prepareStatement(updateSql))
            {
                preparedStatement.setString(1,account.getName());
                preparedStatement.setLong(2,account.getId());

                Debugger.println(this,updateSql+" account:"+account);
                return preparedStatement.executeUpdate();
            }
        }
        catch (SQLException e) {
            throw new DataException(e);
        }
    }

    public boolean deleteAccountById(Long accountId)
    {
        String deleteSql = "DELETE from  app.account where ACCOUNT_ID = ?";
        try(Connection connection = this.supplier.get())
        {
            try(PreparedStatement preparedStatement = connection.prepareStatement(deleteSql))
            {
                preparedStatement.setLong(1,accountId);
                Debugger.println(this,deleteSql);
                return preparedStatement.executeUpdate() > 0;
            }
        }
        catch (SQLException e) {
            throw new DataException(e);
        }
    }

    @Override
    public Account save(Account account)
    {
        if(executeUpdate(account) ==0)
        {
            return create(account);
        }
        return account;
    }
}

