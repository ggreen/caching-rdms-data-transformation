package com.github.ggreen.caching.rdms.domain;
import nyla.solutions.core.patterns.jdbc.Sql;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Supplier;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AccountJdbcRepositoryTest
{
    private Connection connection;
    private Supplier<Connection> supplier;
    private AccountJdbcRepository subject;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet = mock(ResultSet.class);


    @BeforeEach
    void setUp()
    {
        connection = mock(Connection.class);
        supplier = () ->  { return connection; };
        subject = new AccountJdbcRepository(supplier);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);
    }

    @Test
    void findById() throws SQLException
    {

        when(resultSet.next()).thenReturn(true);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        Account account = new Account();

        Long accountId = 1L;
        Account actual = subject.findById(accountId);
        assertNotNull(actual);

    }

    @Test
    void create() throws SQLException
    {
        Account expected = new Account();
        subject.create(expected);
        verify(preparedStatement.executeUpdate());
    }
}