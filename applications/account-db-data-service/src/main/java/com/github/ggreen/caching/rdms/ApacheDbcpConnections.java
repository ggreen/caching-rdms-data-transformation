package com.github.ggreen.caching.rdms;

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

        this.dataSource.setUrl(settings.getProperty("jdbc.url"));
        this.dataSource.setDriverClassName(settings.getProperty("jdbc.driver.class.name"));
        this.dataSource.setUsername(settings.getProperty("jdbc.username"));
        this.dataSource.setPassword(settings.getProperty("jdbc.password"));

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
