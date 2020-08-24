package com.github.ggreen.caching.rdms.pipeline;

import com.github.ggreen.caching.rdms.pipeline.batch.AccountDbBatch;
import nyla.solutions.core.exception.SystemException;
import nyla.solutions.core.patterns.batch.BatchReport;

import java.sql.SQLException;

class AccountCacheBatchApp
{


    public static void main(String[] args)
    {
        try {
            BatchReport batchReport = new AccountDbBatch(new GeodeConsumer())
                    .execute();
            System.out.println(batchReport);
        }
        catch (SQLException e) {
            throw new SystemException(e);
        }
    }

}