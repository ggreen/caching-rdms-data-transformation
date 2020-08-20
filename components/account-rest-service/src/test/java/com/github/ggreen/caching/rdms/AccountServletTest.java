package com.github.ggreen.caching.rdms;

import com.github.ggreen.caching.rdms.domain.Account;
import com.github.ggreen.caching.rdms.domain.AccountRepository;
import com.github.ggreen.caching.rdms.domain.jdbc.AccountJdbcRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AccountServletTest
{
    private AccountRepository repository;
    private AccountServlet subject;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private PrintWriter printWriter;
    private Function<Account, String> acctToJSon;
    private Function<String, Account> jsonToAccount;

    @BeforeEach
    void setUp() throws IOException
    {
        repository = mock(AccountJdbcRepository.class);
        acctToJSon = mock(Function.class);
        jsonToAccount = mock(Function.class);
        subject = new AccountServlet(repository, acctToJSon,jsonToAccount);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        printWriter = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Nested
    class WhenCreate
    {
        @Test
        void given_invalid_account_then_create() throws ServletException, IOException
        {
            BufferedReader reader = mock(BufferedReader.class);
            when(request.getReader()).thenReturn(reader);
            String json = "{}";
            when(reader.readLine()).thenReturn(json).thenReturn(null);

            subject.doPost(request,response);
            verify(jsonToAccount).apply(anyString());
            repository.create(any());
        }
    }

    @Nested
    public class WhenRead
    {
        @Test
        public void given_valid_acctId_Then_Return_account() throws ServletException, IOException, SQLException
        {
            String uri = "/accounts/db/14";

            when(request.getRequestURI()).thenReturn(uri);
            subject.doGet(request, response);
            verify(repository).findById(anyLong());
        }

        @Test
        public void given_invalid_acctId_Then_Return_null() throws ServletException, IOException, SQLException
        {
            String json = "{}";
            when(acctToJSon.apply(any())).thenReturn(json);
            subject.doGet(request, response);


            verify(repository, never()).findById(anyLong());
            verify(response).getWriter();
            verify(acctToJSon).apply(any());
            verify(printWriter).write(anyString());

        }
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