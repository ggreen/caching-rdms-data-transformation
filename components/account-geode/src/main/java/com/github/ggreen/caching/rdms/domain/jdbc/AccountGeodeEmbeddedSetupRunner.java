package com.github.ggreen.caching.rdms.domain.jdbc;

import io.pivotal.services.dataTx.geode.qa.GUnit;
import nyla.solutions.core.exception.FatalException;
import org.apache.geode.cache.RegionShortcut;

public class AccountGeodeEmbeddedSetupRunner implements Runnable, AutoCloseable
{
    private final GUnit gUnit;

    public AccountGeodeEmbeddedSetupRunner()
    {
        this(new GUnit());
    }
    public AccountGeodeEmbeddedSetupRunner(GUnit gUnit)
    {
        this.gUnit = gUnit;
    }

    @Override
    public void run()
    {
        try {
            gUnit.startCluster();

            gUnit.createRegion("accounts", RegionShortcut.PARTITION);
        }
        catch (Exception e) {
            throw new FatalException(e);
        }

    }

    public void close()
    {
        gUnit.shutdown();
    }
}
