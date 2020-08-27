package com.github.ggreen.caching.rdms;

import com.github.ggreen.caching.rdms.domain.Account;
import com.github.ggreen.caching.rdms.domain.AccountRepository;
import com.github.ggreen.caching.rdms.jdbc.AccountJdbcRepository;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.patterns.creational.servicefactory.ServiceFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("Given Account Servlet")
public class AccountServletTest
{
    private AccountRepository repository;
    private AccountRestServlet subject;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private PrintWriter printWriter;
    private Function<Account, String> acctToJSon;
    private Function<String, Account> jsonToAccount;
    private BufferedReader bufferReader;
    private BufferedReader reader;

    @BeforeEach
    void setUp() throws IOException
    {
        repository = mock(AccountJdbcRepository.class);
        acctToJSon = mock(Function.class);
        jsonToAccount = mock(Function.class);
        subject = new AccountRestServlet(repository, acctToJSon,jsonToAccount);
        request = mock(HttpServletRequest.class);

        reader = mock(BufferedReader.class);

        response = mock(HttpServletResponse.class);
        printWriter = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(printWriter);
    }


    @Test
    void createWithServiceFactory()
    {
        ServiceFactory serviceFactory = mock(ServiceFactory.class);

        subject = new AccountRestServlet(serviceFactory);
        verify(serviceFactory).create(anyString());
    }

    @Test
    @DisplayName("When account is null then toAccount returns null")
    void toAccount_when_null() throws IOException
    {
        assertNull(subject.toAccount(request));
    }

    @Nested
    class WhenCreate
    {
        @Test
        void given_invalid_account_then_create() throws ServletException, IOException
        {
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

            String expectedUri = "/accounts/1";
            when(request.getRequestURI()).thenReturn(expectedUri);
            when(repository.findById(anyLong())).thenReturn(null);

            subject.doGet(request, response);
            verify(repository).findById(anyLong());
            verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);

        }
    }

    @Nested
    class WhenUpdate
    {
        @Test
        void given_valid_account_then_update() throws ServletException, IOException
        {
            Account expected = new JavaBeanGeneratorCreator<>
                    (Account.class).randomizeAll().create();

            when(jsonToAccount.apply(any())).thenReturn(expected);
            when(repository.update(any())).thenReturn(expected);
            when(acctToJSon.apply(any())).thenReturn("{}");
            subject.doPut(request, response);

            verify(repository).update(any());
            verify(acctToJSon).apply(any());
            verify(printWriter).write(anyString());

        }
    }
    @Nested
    class WhenDelete
    {
        @Test
        void given_nullAccount_throws_Error() throws ServletException
        {
            try
            {
                subject.doDelete(request, response);
            }catch(NullPointerException e)
            {
                assertTrue(e.getMessage().contains("not found"));
            }
        }

        @Test
        void given_id_delete_account() throws ServletException
        {
            String exceptedId = "1";
            when(request.getRequestURI()).thenReturn("/accounts/"+exceptedId);
            subject.doDelete(request, response);
            verify(repository).deleteAccountById(anyLong());

        }
    }

    @Test
    @DisplayName("When URI is valid Then Return accountID")
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