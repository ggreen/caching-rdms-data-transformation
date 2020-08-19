package com.github.ggreen.caching.rdms;

import com.github.ggreen.caching.rdms.domain.Account;
import com.github.ggreen.caching.rdms.domain.jdbc.AccountJdbcRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.function.Function;

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
    private PrintWriter printWriter;
    private Function<Account, String> converter;

    @BeforeEach
    void setUp() throws IOException
    {
        repository = mock(AccountJdbcRepository.class);
        converter = mock(Function.class);
        subject = new AccountDbServlet(repository, converter);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        printWriter = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    public void when_get_invalid_acctId_Then_Return_null() throws ServletException, IOException, SQLException
    {
        String json = "{}";
        when(converter.apply(any())).thenReturn(json);
        subject.doGet(request,response);


        verify(repository,never()).findById(anyLong());
        verify(response).getWriter();
        verify(converter).apply(any());
        verify(printWriter).write(anyString());

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