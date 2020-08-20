package com.github.ggreen.caching.rdms;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

class AccountAppTest
{
    @BeforeAll
    static void beforeAll()
    {
        System.setProperty("SERVLET_CLASS_NAME",AccountDbServlet.class.getName());
    }

    @Test
    void run() throws InterruptedException, ExecutionException, TimeoutException, IOException
    {
        AccountApp app = AccountApp.getInstance();
        app.start();

        HttpGet httpGet =  new HttpGet("http://localhost:8080");
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = httpclient.execute(httpGet);

        assertEquals(200,response.getStatusLine().getStatusCode());
        String actual = response.toString();
        assertTrue(actual != null  && actual.length() > 0);

        app.stop();

    }
}