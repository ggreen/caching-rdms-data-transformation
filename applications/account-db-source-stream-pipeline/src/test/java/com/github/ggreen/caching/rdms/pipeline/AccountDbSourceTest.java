package com.github.ggreen.caching.rdms.pipeline;

import com.github.ggreen.caching.rdms.pipeline.batch.AccountDbBatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class AccountDbSourceTest
{
    private AccountDbBatch batch;
    private AccountDbSource subject;
    private int sleepSeconds =2;

    @BeforeEach
    void setUp()
    {
        batch = mock(AccountDbBatch.class);
        subject = new AccountDbSource(batch, sleepSeconds);
    }

    @Test
    void execute() throws SQLException
    {
        subject.read();
        verify(batch).execute();
    }

    @Test
    void start() throws SQLException, InterruptedException
    {
        Thread thread = subject.start();
        assertTrue(thread.isAlive());
        long waitPeriodSeconds = 1000*5;
        Thread.sleep(waitPeriodSeconds);
        verify(batch,atLeast(2)).execute();
    }
}