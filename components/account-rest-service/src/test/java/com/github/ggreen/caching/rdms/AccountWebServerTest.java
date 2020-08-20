package com.github.ggreen.caching.rdms;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServlet;
import java.net.MalformedURLException;
import java.net.URL;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AccountWebServerTest
{
    private Server server;
    private AccountWebServer subject;
    private ServletContextHandler context;
    private URL url;


    @BeforeEach
    void setUp() throws MalformedURLException
    {
        String pathPattern = "/hello/world";
        url = new URL("http://localhost/");
        Class<? extends HttpServlet> servlet = HttpServlet.class;
        server = mock(Server.class);
        context = mock(ServletContextHandler.class);
        subject = new AccountWebServer(servlet,pathPattern);

    }



    @Test
    void construct() throws Exception
    {
        subject.constructServerContext(server,context,url);

        verify(context,times(2)).addServlet(any(Class.class),anyString());
        verify(server).setHandler(any());
    }


}