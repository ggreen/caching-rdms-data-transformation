package com.github.ggreen.caching.rdms;

import nyla.solutions.core.util.Config;

import javax.servlet.http.HttpServlet;

class AccountApp
{
    private final AccountWebServer accountWebServer;

    AccountApp(AccountWebServer accountWebServer)
    {
        this.accountWebServer = accountWebServer;
    }

    static AccountApp getInstance()
    {
        Class<? extends HttpServlet> servletClass = Config.getPropertyClass("SERVLET_CLASS_NAME",AccountDbServlet.class);

        //Class<? extends HttpServlet> servletClass, String pathPattern
        return new AccountApp(new AccountWebServer(
                servletClass,
                Config.getProperty("SERVLET_PATH_PATTERN","/accounts/*")
        ));

    }

    void start()
    {
        accountWebServer.start();
    }

    void stop()
    {
        accountWebServer.stop();
    }



    private void run()
    {
        try {
            accountWebServer.run();
        }
        catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args)
    {
        getInstance().run();
    }
}