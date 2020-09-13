package com.github.ggreen.caching.rdms.jdbc;

import com.github.ggreen.caching.rdms.domain.Account;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.util.Scheduler;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AccountResultSetConverterTest
{

    @Test
    void convert() throws SQLException
    {
        Account expected = new JavaBeanGeneratorCreator<>(Account.class).randomizeAll()
                                                                        .create();
        AccountResultSetConverter subject = new AccountResultSetConverter();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getLong("ACCOUNT_ID")).thenReturn(expected.getId());
        when(resultSet.getString("ACCOUNT_NM")).thenReturn(expected.getName());
        when(resultSet.getTimestamp("ACCOUNT_TIMESTAMP")).thenReturn(
                Scheduler.toTimestamp(expected.getCurrentTimestamp()));

        Account actual = subject.convert(resultSet);
        verify(resultSet,never()).next();
        assertEquals(expected,actual);


    }
}