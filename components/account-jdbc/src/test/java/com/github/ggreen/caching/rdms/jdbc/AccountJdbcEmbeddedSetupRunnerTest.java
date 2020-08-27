package com.github.ggreen.caching.rdms.jdbc;

import nyla.solutions.core.patterns.jdbc.Sql;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.mockito.Mockito.*;

/**
 * @author Gregory Green
 */
class AccountJdbcEmbeddedSetupRunnerTest
{

    @Test
    void run() throws SQLException
    {
        ApacheDbcpConnections connections = mock(ApacheDbcpConnections.class);
        Sql sql = mock(Sql.class);
        AccountJdbcEmbeddedSetupRunner subject = new AccountJdbcEmbeddedSetupRunner(connections,sql);
        subject.run();
        verify(connections,atLeastOnce()).get();
        verify(sql,atLeastOnce()).execute(any(),anyString());
    }
}