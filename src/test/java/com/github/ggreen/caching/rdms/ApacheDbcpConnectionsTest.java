package com.github.ggreen.caching.rdms;

import nyla.solutions.core.util.settings.Settings;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ApacheDbcpConnectionsTest
{
    @Test
    void get() throws SQLException
    {
        BasicDataSource basicDataSource = mock(BasicDataSource.class);
        Settings settings = mock(Settings.class);
        when(settings.getProperty(anyString())).thenReturn("any");

        ApacheDbcpConnections subject = new ApacheDbcpConnections(basicDataSource,settings);
        subject.get();
        verify(basicDataSource).getConnection();
        verify(basicDataSource).setDriverClassName(anyString());
        verify(basicDataSource).setUrl(anyString());
        verify(basicDataSource).setUsername(anyString());
        verify(basicDataSource).setPassword(anyString());
        verify(settings,atLeastOnce()).getProperty(anyString());

    }
}