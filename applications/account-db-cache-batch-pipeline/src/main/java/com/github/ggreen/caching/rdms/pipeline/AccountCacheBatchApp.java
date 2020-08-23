package com.github.ggreen.caching.rdms.pipeline;

import com.github.ggreen.caching.rdms.domain.Account;
import com.github.ggreen.caching.rdms.domain.jdbc.ApacheDbcpConnections;
import nyla.solutions.core.exception.SystemException;
import nyla.solutions.core.patterns.batch.BatchJob;
import nyla.solutions.core.patterns.batch.BatchReport;
import nyla.solutions.core.patterns.jdbc.batch.SelectResultSetConverterSupplier;

import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

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