package com.github.ggreen.caching.rdms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ggreen.caching.rdms.domain.Account;
import com.github.ggreen.caching.rdms.jdbc.AccountJdbcEmbeddedSetupRunner;
import com.github.ggreen.caching.rdms.migration.AccountDbMigrationApp;
import nyla.solutions.core.net.http.Http;
import nyla.solutions.core.net.http.HttpResponse;
import nyla.solutions.core.patterns.creational.generator.FullNameCreator;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.util.Config;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AccountAppTest
{
    private static Thread thread;
    private static AccountApp app;
    private static Runnable embeddedSetup;
    private Http http = new Http();

    private Account expected;
    private String expectedJson;
    private String serverPort = Config.getProperty("SERVER_PORT");

    @BeforeAll
    public static void init()
    {
        String[] args = {};
        AccountDbMigrationApp.main(args);
    }

    @BeforeEach
    void setUp() throws JsonProcessingException
    {
        expected = new JavaBeanGeneratorCreator<>(Account.class)
                .randomizeAll().create();

        expectedJson = toAccountJson();

        new AccountJdbcEmbeddedSetupRunner().run();


    }

    @BeforeAll
    static void beforeAll() throws IOException, SQLException
    {
        String[] args = {};
        AccountDbMigrationApp.main(args);
        new AccountJdbcEmbeddedSetupRunner().run();

        app = AccountApp.getInstance();

        thread = new Thread(() -> app.run());
        thread.start();
    }

    @Test
    void home_returns_200() throws InterruptedException, ExecutionException, TimeoutException, IOException
    {

        String uri = "http://localhost:"+serverPort+"";
        HttpResponse response = http.get(new URL(uri));

        assertEquals(200, response.getStatusCode());
        String actual = response.toString();
        assertTrue(actual != null && actual.length() > 0);

    }

    @Test
    void when_post_then_create_account() throws IOException
    {
        String uri = "http://localhost:"+serverPort+"/accounts";

        String expectedJson = toAccountJson();
        verifyPost(uri, expectedJson);
    }

    private void verifyPost(String uri, String expectedJson) throws IOException
    {
        HttpResponse response = http.post(new URL(uri), expectedJson);
        assertEquals(200, response.getStatusCode());
    }


    @Test
    void when_post_then_create_then_update_account() throws IOException
    {
        String uri = "http://localhost:"+serverPort+"/accounts";
        String expectedJson = toAccountJson();
        verifyPost(uri, expectedJson);
        verifyPost(uri, expectedJson);
    }


    @Test
    void when_put_then_update_account() throws IOException
    {

        String createJSon = expectedJson;
        createAccount(createJSon);
        expected.setName("Updated");

        String uri = "http://localhost:"+serverPort+"/accounts/"+expected.getId();

        expectedJson = toAccountJson();
        HttpResponse httpResponse = http.put(new URL(uri),expectedJson);
        assertTrue(httpResponse.isOk());

        httpResponse = http.get(new URL(uri));
        assertTrue(httpResponse.isOk());
        assertThat(httpResponse.getBody()).contains(expected.getName());
    }

    @Test
    void when_delete_then_delete_account() throws IOException
    {

        String createJSon = expectedJson;
        createAccount(createJSon);
        expected.setName(new FullNameCreator().create());

        String uri = "http://localhost:"+serverPort+"/accounts/" + expected.getId();
        HttpResponse response =http.delete(new URL(uri));

        assertThrows(FileNotFoundException.class, () -> http.get(new URL(uri)));

    }


    private String createAccount(String expectedJson) throws IOException
    {
        String uri = "http://localhost:"+serverPort+"/accounts";

       HttpResponse response = http.post(new URL(uri),expectedJson);
        assertTrue(response.isOk());

         return response.getBody();
    }

    private String toAccountJson() throws JsonProcessingException
    {
        ObjectMapper om = new ObjectMapper();
        String expectedJson = om.writeValueAsString(expected);
        return expectedJson;
    }


    @AfterAll
    static void tearDown() throws Exception
    {
        if(app == null)
            return;

        app.stop();
        thread.join();

        if (embeddedSetup instanceof AutoCloseable) {
            ((AutoCloseable) embeddedSetup).close();
        }
    }

    @Test
    void getInstance()
    {
        assertEquals(AccountApp.getInstance(), AccountApp.getInstance());
    }
}