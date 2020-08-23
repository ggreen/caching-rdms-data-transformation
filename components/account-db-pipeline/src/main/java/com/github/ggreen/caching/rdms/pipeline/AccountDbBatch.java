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

class AccountDbBatch
{
    private final SelectResultSetConverterSupplier<Account> supplier;
    private final static String selectSql = "select * from APP.ACCOUNT";
    private Consumer<List<Account>> consumer;


    public AccountDbBatch(Consumer<List<Account>> consumer) throws SQLException
    {
        this( new SelectResultSetConverterSupplier<Account>(
                new ApacheDbcpConnections(),
                new AccountResultSetConverter(),
                selectSql),
                consumer);
    }
    public AccountDbBatch(SelectResultSetConverterSupplier<Account> supplier, Consumer<List<Account>> consumer)
            throws SQLException
    {
        this.supplier = supplier;

        this.consumer = consumer;
    }

    public BatchReport execute() throws SQLException
    {

        BatchJob<Account, Account> job
                = BatchJob.builder()
                          .supplier(supplier)
                          .consumer(consumer)
                          .build();

        return job.execute();

    }


}