package com.github.ggreen.caching.rdms.pipeline.batch;

import com.github.ggreen.caching.rdms.domain.Account;
import com.github.ggreen.caching.rdms.geode.AccountGeodeRepository;
import com.github.ggreen.caching.rdms.jdbc.AccountResultSetConverter;
import com.github.ggreen.caching.rdms.jdbc.ApacheDbcpConnections;
import nyla.solutions.core.patterns.batch.BatchJob;
import nyla.solutions.core.patterns.batch.BatchReport;
import nyla.solutions.core.patterns.jdbc.batch.SelectResultSetConverterSupplier;
import nyla.solutions.core.util.Text;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Gregory Green
 */
public class AccountDbBatch
{
    private final SelectResultSetConverterSupplier<Account> supplier;
    private final AccountGeodeRepository geodeRepository;
    private final Consumer<List<Account>> consumer;
    private String sql;

    public AccountDbBatch(SelectResultSetConverterSupplier<Account> supplier, Consumer<List<Account>> consumer,
                          AccountGeodeRepository geodeRepository)
    {
        this.supplier = supplier;

        this.consumer = consumer;

        this.geodeRepository = geodeRepository;
    }
    public AccountDbBatch(Consumer<List<Account>> consumer) throws SQLException
    {
        this(consumer,new AccountGeodeRepository());

    }
    public AccountDbBatch(Consumer<List<Account>> consumer, AccountGeodeRepository geodeRepository) throws SQLException
    {
        this( new SelectResultSetConverterSupplier<>(
                new ApacheDbcpConnections(),
                new AccountResultSetConverter(),
                "select * from APP.ACCOUNT"),
                consumer,
                geodeRepository);
    }

    protected void constructCdcSupplier()
    {
        StringBuilder sql = new StringBuilder().append("select * from APP.ACCOUNT");
        Long[] maxIdAndTime= geodeRepository.selectMaxAccountIdAndTimestamp();
        Long maxId = null;
        Timestamp maxTimestamp = null;

        if(maxIdAndTime != null && maxIdAndTime.length > 0)
        {
            if(maxIdAndTime[0] != null)
            {
                maxId =  maxIdAndTime[0];
                sql.append(" WHERE ACCOUNT_ID> ?");
            }

            if(maxIdAndTime[1] != null)
            {
                maxTimestamp = new Timestamp(maxIdAndTime[1]);

                if(maxIdAndTime[0] != null)
                    sql.append(" OR ");
                else
                    sql.append(" WHERE ");

                sql.append(" DATE_TRUNC('MILLISECOND',ACCOUNT_TIMESTAMP) > ?");
            }

        }

        this.supplier.setParameters(maxId,maxTimestamp);

        this.sql = sql.toString();
        this.supplier.setSql(sql.toString());

    }

    public BatchReport execute()
    {
        constructCdcSupplier();
        System.out.println(this.sql);

        BatchJob<Account, Account> job
                = BatchJob.builder()
                          .supplier(supplier)
                          .consumer(consumer)
                          .build();

        return job.execute();

    }


    protected String getSql()
    {
        return this.sql;
    }
}