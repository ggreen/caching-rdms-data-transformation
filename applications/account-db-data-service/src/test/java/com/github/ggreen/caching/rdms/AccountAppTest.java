package com.github.ggreen.caching.rdms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ggreen.caching.rdms.domain.Account;
import nyla.solutions.core.patterns.creational.generator.FullNameCreator;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.patterns.creational.servicefactory.ConfigServiceFactory;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountAppTest
{
    private static Thread thread;
    private static AccountApp app;
    private static Runnable embeddedSetup;

    private Account expected;
    private String expectedJson;

    @BeforeEach
    void setUp() throws JsonProcessingException
    {
        expected = new JavaBeanGeneratorCreator<>(Account.class)
                .randomizeAll().create();

        expectedJson = toAccountJson();
    }

    @BeforeAll
    static void beforeAll() throws IOException, SQLException
    {
        embeddedSetup = ConfigServiceFactory
                .getConfigServiceFactoryInstance().create("embeddedSetup");

        embeddedSetup.run();


        app = AccountApp.getInstance();

        thread = new Thread(()-> app.run());
        thread.start();


    }



    @Test
    void home_returns_200() throws InterruptedException, ExecutionException, TimeoutException, IOException
    {

        String uri = "http://localhost:8080";
        try(CloseableHttpResponse response = requestHttp(new HttpGet(uri)))
        {
            assertEquals(200,response.getStatusLine().getStatusCode());
            String actual = response.toString();
            assertTrue(actual != null  && actual.length() > 0);
        }
    }

    @Test
    void when_post_then_create_account() throws IOException
    {
        String uri = "http://localhost:8080/accounts";
        HttpPost httpPost = new HttpPost(uri);
        String expectedJson = toAccountJson();
        verifyCreateAccount(httpPost, expectedJson);
    }

    private void verifyCreateAccount(HttpPost httpPost, String expectedJson) throws IOException
    {
        StringEntity httpEntity = new StringEntity(expectedJson);
        httpPost.setEntity(httpEntity);

        try(CloseableHttpResponse response = requestHttp(httpPost))
        {
            assertEquals(200,response.getStatusLine().getStatusCode());
            String actual = response.toString();
            assertTrue(actual != null  && actual.length() > 0);
        }
    }

    @Test
    void when_put_then_update_account() throws IOException
    {

        String createJSon = expectedJson;
        createAccount(createJSon);
        expected.setName(new FullNameCreator().create());

        String uri = "http://localhost:8080/accounts";
        HttpPut httpPut = new HttpPut(uri);
        expectedJson = toAccountJson();

        StringEntity httpEntity = new StringEntity(expectedJson);
        httpPut.setEntity(httpEntity);
        String actual;
        try(CloseableHttpResponse response = requestHttp(httpPut))
        {
            assertEquals(200,response.getStatusLine().getStatusCode());
            actual = response.toString();
        }

    }
    @Test
    void when_delete_then_delete_account() throws IOException
    {

        String createJSon = expectedJson;
        createAccount(createJSon);
        expected.setName(new FullNameCreator().create());

        String uri = "http://localhost:8080/accounts/"+expected.getId();
        HttpDelete httpDelete = new HttpDelete(uri);
        expectedJson = toAccountJson();

        verifyCreateAccount(new HttpPost(uri), expectedJson);


        StringEntity httpEntity = new StringEntity(expectedJson);
        String actual;
        try(CloseableHttpResponse response = requestHttp(httpDelete))
        {
            assertEquals(200,response.getStatusLine().getStatusCode());
            actual = response.toString();
        }

        try(CloseableHttpResponse response = requestHttp(new HttpGet(uri)))
        {
            assertEquals(HttpServletResponse.SC_NOT_FOUND,response.getStatusLine().getStatusCode());
        }

    }


    private String createAccount(String expectedJson) throws IOException
    {
        String uri = "http://localhost:8080/accounts";
        HttpPost httpPost = new HttpPost(uri);
        StringEntity httpEntity = new StringEntity(expectedJson);
        httpPost.setEntity(httpEntity);
        String actual;
        try(CloseableHttpResponse response = requestHttp(httpPost))
        {
            assertEquals(200,response.getStatusLine().getStatusCode());
            actual = response.toString();
        }
        return actual;
    }

    private String toAccountJson() throws JsonProcessingException
    {
        ObjectMapper om = new ObjectMapper();
        String expectedJson = om.writeValueAsString(expected);
        return expectedJson;
    }

    private CloseableHttpResponse requestHttp(HttpUriRequest request) throws IOException
    {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = httpclient.execute(request);
        return response;
    }

    @AfterAll
    static void tearDown() throws Exception
    {
        app.stop();
        thread.join();

        if(embeddedSetup instanceof AutoCloseable)
        {
            ((AutoCloseable)embeddedSetup).close();
        }
    }

    @Test
    void getInstance()
    {
        assertEquals(AccountApp.getInstance(),AccountApp.getInstance());
    }
}