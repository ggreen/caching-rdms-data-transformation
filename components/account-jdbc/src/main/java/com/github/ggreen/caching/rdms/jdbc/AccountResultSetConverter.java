package com.github.ggreen.caching.rdms.jdbc;

import com.github.ggreen.caching.rdms.domain.Account;
import nyla.solutions.core.exception.DataException;
import nyla.solutions.core.patterns.conversion.Converter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * @author Gregory Green
 */
public class AccountResultSetConverter implements Converter<ResultSet, Account>
{
    private static final String ACCOUNT_ID_COL = "ACCOUNT_ID";
    private static final String ACCOUNT_NM_COL = "ACCOUNT_NM";
    private static final String CURRENT_TIMESTAMP_COL = "ACCOUNT_TIMESTAMP";

    @Override
    public Account convert(ResultSet resultSet)
    {
        try {
            Timestamp timestamp = resultSet.getTimestamp(CURRENT_TIMESTAMP_COL);

            return new Account(
                    resultSet.getLong(ACCOUNT_ID_COL),
                    resultSet.getString(ACCOUNT_NM_COL),
                    timestamp != null? timestamp.getTime(): null);
        }
        catch (SQLException e) {
            throw new DataException(e);
        }
    }
}
