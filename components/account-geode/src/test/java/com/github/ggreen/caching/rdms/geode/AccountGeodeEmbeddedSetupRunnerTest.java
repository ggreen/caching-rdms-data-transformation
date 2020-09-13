package com.github.ggreen.caching.rdms.geode;

import io.pivotal.services.dataTx.geode.qa.GUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Disabled
class AccountGeodeEmbeddedSetupRunnerTest
{
    private GUnit gunit;
    private AccountGeodeEmbeddedSetupRunner subject;

    @BeforeEach
    void setUp()
    {
        gunit = mock(GUnit.class);
        subject = new AccountGeodeEmbeddedSetupRunner(gunit);
    }

    @BeforeAll
    static void beforeAll()
    {
        System.setProperty("gfsh_location",".");
    }

    @Test
    void run() throws Exception
    {
        subject.run();
        verify(gunit).startCluster();
        verify(gunit).createRegion(anyString(),any());
    }

    @Test
    void close()
    {
        subject.close();
        verify(gunit).shutdown();
    }
}