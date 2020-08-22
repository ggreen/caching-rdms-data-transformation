package com.github.ggreen.caching.rdms.pipeline;

import com.github.ggreen.caching.rdms.domain.Account;
import nyla.solutions.core.exception.DataException;
import nyla.solutions.core.patterns.conversion.Converter;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountResultSetConverter implements Converter<ResultSet, Account>
{
    private static final String ACCOUNT_ID_COL = "ACCOUNT_ID";
    private static final String ACCOUNT_NM_COL = "ACCOUNT_NM";

    @Override
    public Account convert(ResultSet resultSet)
    {
        try {
            return new Account(resultSet.getLong(ACCOUNT_ID_COL),resultSet.getString(ACCOUNT_NM_COL));
        }
        catch (SQLException e) {
            throw new DataException(e);
        }
    }
}
