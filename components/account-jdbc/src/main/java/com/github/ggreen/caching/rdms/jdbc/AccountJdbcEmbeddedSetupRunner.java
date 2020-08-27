package com.github.ggreen.caching.rdms.jdbc;

import nyla.solutions.core.exception.CommunicationException;
import nyla.solutions.core.exception.SetupException;
import nyla.solutions.core.io.IO;
import nyla.solutions.core.patterns.jdbc.Sql;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @author Gregory Green
 */
public class AccountJdbcEmbeddedSetupRunner implements  Runnable
{
    private final ApacheDbcpConnections connections;
    private final Sql sql;

    public AccountJdbcEmbeddedSetupRunner()
    {
        this(new ApacheDbcpConnections(), new Sql());
    }
    protected AccountJdbcEmbeddedSetupRunner(ApacheDbcpConnections connections, Sql sql)
    {
        this.connections = connections;
        this.sql = sql;
    }

    public  void run()
    {
        try {
            String setUpSql = IO.readFile("../../applications/account-db-migrations/src/main/resources/db/migration/V1__INIT_ACCT_DB.sql");

            String dropSql = "DROP SCHEMA IF EXISTS APP CASCADE";
            sql.execute(connections.get(),dropSql);
            sql.execute(connections.get(),setUpSql);
        }
        catch (IOException e) {
           throw new SetupException(e);
        }
        catch (SQLException e) {
            throw new CommunicationException(e);
        }
    }
}
