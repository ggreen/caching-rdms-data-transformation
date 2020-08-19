package com.github.ggreen.caching.rdms;

import com.github.ggreen.caching.rdms.domain.AccountJdbcRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AccountDbServletTest
{
    private AccountJdbcRepository repository;
    private AccountDbServlet subject;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setUp()
    {
        repository = mock(AccountJdbcRepository.class);
        subject = new AccountDbServlet(repository);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    public void when_get_invalid_acctId_Then_Return_null() throws ServletException, IOException, SQLException
    {
        subject.doGet(request,response);
        verify(repository,never()).findById(anyLong());
    }

    @Test
    public void when_get_valid_acctId_Then_Return_account() throws ServletException, IOException, SQLException
    {
        String uri = "/accounts/db/14";

        when(request.getRequestURI()).thenReturn(uri);
        subject.doGet(request,response);
        verify(repository).findById(anyLong());
    }

    @Test
    void accountId()
    {
        Long expected = 123L;
        String uri = "/db/"+expected;
        when(request.getRequestURI()).thenReturn(uri);
        Long actual = subject.accountId(request);
        assertEquals(expected,actual);

        when(request.getRequestURI()).thenReturn("/");
        actual = subject.accountId(request);
        assertNull(actual);
    }
}