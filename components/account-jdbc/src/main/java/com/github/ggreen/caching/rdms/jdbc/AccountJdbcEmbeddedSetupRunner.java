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
            sql.execute(connections.get(),"delete from app.account");
        }
        catch (SQLException e) {
            throw new CommunicationException(e);
        }
    }
}
