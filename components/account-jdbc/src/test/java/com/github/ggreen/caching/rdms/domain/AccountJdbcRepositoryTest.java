package com.github.ggreen.caching.rdms.domain;
import com.github.ggreen.caching.rdms.domain.jdbc.AccountJdbcRepository;
import nyla.solutions.core.exception.DataException;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
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
    void setUp() throws SQLException
    {
        connection = mock(Connection.class);
        supplier = () ->  { return connection; };
        subject = new AccountJdbcRepository(supplier);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
    }

    @Test
    void findById() throws SQLException
    {

        when(resultSet.next()).thenReturn(true);

        Account account = new Account();

        Long accountId = 1L;
        Account actual = subject.findById(accountId);
        assertNotNull(actual);

    }

    @Test
    void create() throws SQLException
    {
        Account expected = new JavaBeanGeneratorCreator<Account>(Account.class)
                .randomizeAll().create();

        Account actual = subject.create(expected);
        assertNotNull(actual);
        verify(preparedStatement).setLong(1, expected.getId());
        verify(preparedStatement).setString(2, expected.getName());
        verify(preparedStatement).execute();
    }

    @Test
    void update() throws SQLException
    {
        Account expected = new JavaBeanGeneratorCreator<Account>(Account.class)
                .randomizeAll().create();

        Account actual = subject.update(expected);
        assertNotNull(actual);
        verify(preparedStatement).setLong(2, expected.getId());
        verify(preparedStatement).setString(1, expected.getName());
        verify(preparedStatement).execute();
    }

    @Test
    void when_sqlexception_then_throw_dataException() throws SQLException
    {
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);

        assertThrows( DataException.class,() -> subject.findById(anyLong()));
    }

    @Test
    void deleteAccountById() throws SQLException
    {
        Long expected = 2L;
        assertTrue(subject.deleteAccountById(expected));
        verify(preparedStatement).setLong(1, expected);
        verify(preparedStatement).execute();
    }

    @Test
    void deleteAccountById_when_given_invalid_id_return_false() throws SQLException
    {
        Long expected = 2L;
        when(preparedStatement.executeUpdate()).thenReturn(0);
        assertFalse(subject.deleteAccountById(expected));
        verify(preparedStatement).setLong(1, expected);
        verify(preparedStatement).executeUpdate();
    }

}