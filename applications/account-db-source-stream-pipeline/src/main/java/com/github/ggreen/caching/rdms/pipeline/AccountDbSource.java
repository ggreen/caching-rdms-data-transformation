package com.github.ggreen.caching.rdms.pipeline;


import com.github.ggreen.caching.rdms.pipeline.batch.AccountDbBatch;
import nyla.solutions.core.patterns.batch.BatchReport;
import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.Debugger;

import java.sql.SQLException;

public class AccountDbSource implements Runnable
{
    private final AccountDbBatch batch;
    private final int sleepSeconds;

    public AccountDbSource() throws SQLException
    {
        this.batch = new AccountDbBatch(new AccountKafkaSender());
        sleepSeconds = Config.getPropertyInteger("ACCOUNT_SOURCE_FETCH_SECS",5);
    }
    public AccountDbSource(AccountDbBatch batch, int sleepSeconds)
    {
        this.batch = batch;
        this.sleepSeconds = sleepSeconds;
    }

    public BatchReport read() throws SQLException
    {
        return batch.execute();
    }

    public Thread start()
    {
        Thread thread = new Thread(this);
        thread.start();
        return thread;

    }

    @Override
    public void run()
    {
        while(true){
            try {
                System.out.println(read());

                Thread.sleep(sleepSeconds*1000);
            }
            catch (Exception e) {
                Debugger.printError(e);
            }

        }
    }
}