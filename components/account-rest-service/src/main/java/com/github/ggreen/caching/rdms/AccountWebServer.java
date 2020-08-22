package com.github.ggreen.caching.rdms;

import java.net.URI;
import java.net.URL;

import nyla.solutions.core.exception.FatalException;
import nyla.solutions.core.exception.SystemException;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import javax.servlet.http.HttpServlet;

public class AccountWebServer
{
    private final Class<? extends HttpServlet> servletClass;
    private final String pathPattern;
    private final Server server;

    public AccountWebServer(Class<? extends HttpServlet> servletClass, String pathPattern)
    {
        this.servletClass = servletClass;
        this.pathPattern = pathPattern;

        server = new Server(new QueuedThreadPool(200));

        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.addConnector(connector);

    }

    public void run() throws Exception
    {
        URL webRootLocation = this.getClass().getResource("/webroot/index.html");
        if (webRootLocation == null) {
            throw new IllegalStateException("Unable to determine webroot URL location");
        }

        ServletContextHandler context = new ServletContextHandler();
        context.setWelcomeFiles(new String[]{"index.html"});

        context.getMimeTypes().addMimeMapping("txt", "text/plain;charset=utf-8");
        constructServerContext(server, context, webRootLocation);

        // Start Server
       start();
        server.join();

    }

    protected void constructServerContext(Server server, ServletContextHandler context, URL webRootLocation) throws Exception
    {
        URI webRootUri = URI.create(webRootLocation.toURI().toASCIIString().replaceFirst("/index.html$", "/"));
        System.err.printf("Web Root URI: %s%n", webRootUri);
        context.setContextPath("/");
        context.setBaseResource(Resource.newResource(webRootUri));

        server.setHandler(context);

        // Add Servlet endpoints
        context.addServlet(servletClass, pathPattern);

        context.addServlet(DefaultServlet.class, "/");

    }
    public void stop()
    {
        try {
            server.stop();
        }
        catch (Exception e) {
            throw new FatalException(e);
        }
    }
    public void start()
    {
        try {
            server.start();
        }
        catch (Exception e) {
            throw new FatalException(e);
        }
    }
}