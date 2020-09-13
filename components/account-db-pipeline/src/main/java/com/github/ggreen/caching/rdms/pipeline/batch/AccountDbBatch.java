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
                constructCdcSql(geodeRepository)),
                consumer,
                geodeRepository);
    }

    protected static String constructCdcSql(AccountGeodeRepository geodeRepository)
    {
        StringBuilder sql = new StringBuilder().append("select * from APP.ACCOUNT");
        Long[] maxIdAndTime= geodeRepository.selectMaxAccountIdAndTimestamp();

        if(maxIdAndTime == null){
            return sql.toString();
        }

        if(maxIdAndTime[0] != null)
        {
            sql.append(" WHERE ACCOUNT_ID>").append(maxIdAndTime[0]);

        }

        if(maxIdAndTime[1] != null)
        {
            if(maxIdAndTime[0] != null)
                sql.append(" OR ");
            else
                sql.append(" WHERE ");

            Date date = new Date(maxIdAndTime[1]);
            String timeText = Text.formatDate("YYYY-MM-dd-HH.mm.ss.SSS",date);
            sql.append(" DATE_TRUNC('MILLISECOND',ACCOUNT_TIMESTAMP) >  TIMESTAMP('").append(timeText).append("')");
        }

        return sql.toString();

    }

    public BatchReport execute()
    {
        String sql =constructCdcSql(geodeRepository);
        System.out.println(sql);
        this.supplier.setSql(sql);

        BatchJob<Account, Account> job
                = BatchJob.builder()
                          .supplier(supplier)
                          .consumer(consumer)
                          .build();

        return job.execute();

    }


}