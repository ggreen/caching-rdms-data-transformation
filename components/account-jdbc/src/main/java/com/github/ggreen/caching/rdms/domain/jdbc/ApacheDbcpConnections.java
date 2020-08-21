package com.github.ggreen.caching.rdms.domain.jdbc;

import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.settings.Settings;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

public class ApacheDbcpConnections implements Supplier<Connection>
{
    private final BasicDataSource dataSource;

    public ApacheDbcpConnections()
    {
        this(new BasicDataSource(), Config.getSettings());
    }
    public ApacheDbcpConnections(BasicDataSource dataSource, Settings settings)
    {
        this.dataSource = dataSource;

        this.dataSource.setUrl(settings.getProperty("JDBC_URL"));
        this.dataSource.setDriverClassName(settings.getProperty("JDBC_DRIVER_CLASS_NAME"));
        this.dataSource.setUsername(settings.getProperty("JDBC_USERNAME"));
        this.dataSource.setPassword(settings.getProperty("JDBC_PASSWORD"));

    }

    @Override
    public Connection get()
    {
        try {
            return dataSource.getConnection();
        }
        catch (SQLException e) {
           throw new RuntimeException(e);
        }
    }
}
