package com.github.ggreen.caching.rdms;

import nyla.solutions.core.util.Config;

import javax.servlet.http.HttpServlet;

public class AccountApp
{
    private final AccountWebServer accountWebServer;

    public AccountApp(AccountWebServer accountWebServer)
    {
        this.accountWebServer = accountWebServer;
    }

    public static AccountApp getInstance()
    {
        Class<? extends HttpServlet> servletClass = Config.getPropertyClass("SERVLET_CLASS_NAME",AccountDbServlet.class);

        //Class<? extends HttpServlet> servletClass, String pathPattern
        return new AccountApp(new AccountWebServer(
                servletClass,
                Config.getProperty("SERVLET_PATH_PATTERN","/accounts*")
        ));

    }

    public void start()
    {
        accountWebServer.start();
    }

    public void stop()
    {
        accountWebServer.stop();
    }

    public static void main(String[] args)
    {
        getInstance().start();
    }
}