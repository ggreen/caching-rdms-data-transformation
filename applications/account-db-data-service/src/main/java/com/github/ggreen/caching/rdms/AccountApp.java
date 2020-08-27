package com.github.ggreen.caching.rdms;

import nyla.solutions.core.exception.FatalException;
import nyla.solutions.core.exception.SystemException;
import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.Debugger;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Gregory Green
 */
class AccountApp
{
    private static AccountApp app;
    private static Lock lock = new ReentrantLock();
    private static long lockSeconds = 1;

    private final AccountWebServer accountWebServer;

    protected AccountApp(AccountWebServer accountWebServer)
    {
        this.accountWebServer = accountWebServer;
    }

     static AccountApp getInstance()
    {
        try
        {
            if(!lock.tryLock(lockSeconds, TimeUnit.SECONDS))
                throw new SystemException("Unable to lock");

            if(app != null)
                return app;


            //Class<? extends HttpServlet> servletClass, String pathPattern
            app = new AccountApp(new AccountWebServer(
                    AccountRestServlet.class,
                    Config.getProperty("SERVLET_PATH_PATTERN","/accounts/*")
            ));

            return app;
        }
        catch (InterruptedException e) {
            throw new FatalException(e);
        }
        finally {
            lock.unlock();
        }


    }

    void start()
    {
        Debugger.printInfo("STARTING SERVER");
        accountWebServer.start();
    }

    void stop()
    {
        Debugger.printInfo("STOPPING SERVER");
        accountWebServer.stop();
        Debugger.printInfo("Goodbyes");
    }



    protected void run()
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