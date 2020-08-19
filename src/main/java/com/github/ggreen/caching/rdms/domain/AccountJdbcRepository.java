package com.github.ggreen.caching.rdms.domain;

import nyla.solutions.core.patterns.jdbc.Sql;

import java.sql.*;
import java.util.Map;
import java.util.function.Supplier;

public class AccountJdbcRepository
{
    private final Supplier<Connection> supplier;

    public AccountJdbcRepository(Supplier<Connection> supplier)
    {
        this.supplier = supplier;
    }


    public Account findById(Long accountId) throws SQLException
    {
        String sqlText = "select id,name from app.account  where ACCOUNT_ID = ?";

        try(Connection connection = supplier.get())
        {
            try(PreparedStatement statement = connection.prepareStatement(sqlText) )
            {
                statement.setLong(1,accountId);
                try(ResultSet resultSet = statement.executeQuery())
                {
                    if(!resultSet.next())
                        return null;

                    return new Account(resultSet.getLong(1),
                            resultSet.getString(2));

                }
            }

        }
    }

    public void create(Account account) throws SQLException
    {
        String insertSql = "INSERT INTO app.account(id, name) values(?,?)";
        try(Connection connection = this.supplier.get())
        {

            try(PreparedStatement preparedStatement = connection.prepareStatement(insertSql))
            {
                preparedStatement.setLong(1,account.getId());
                preparedStatement.setString(1,account.getName());

                preparedStatement.execute();
            }
        }
    }
}
