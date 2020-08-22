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
    private final SelectResultSetConverterSupplier<Account> supplier;
    private final static String selectSql = "select * from APP.ACCOUNT";
    private Consumer<List<Account>> geodeConsumer;


    public AccountCacheBatchApp() throws SQLException
    {
        this( new SelectResultSetConverterSupplier<Account>(
                new ApacheDbcpConnections(),
                new AccountResultSetConverter(),
                selectSql),
                new GeodeConsumer());
    }
    public AccountCacheBatchApp(SelectResultSetConverterSupplier<Account> supplier, Consumer<List<Account>> geodeConsumer)
            throws SQLException
    {
        this.supplier = supplier;

        this.geodeConsumer = geodeConsumer;
    }

    public BatchReport execute() throws SQLException
    {

        BatchJob<Account, Account> job
                = BatchJob.builder()
                          .supplier(supplier)
                          .consumer(geodeConsumer)
                          .build();

        return job.execute();

    }

    public static void main(String[] args)
    {
        try {
            BatchReport batchReport = new AccountCacheBatchApp().execute();
            System.out.println(batchReport);
        }
        catch (SQLException e) {
            throw new SystemException(e);
        }
    }

}