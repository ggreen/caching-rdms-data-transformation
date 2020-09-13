package com.github.ggreen.caching.rdms.pipeline.batch;

import com.github.ggreen.caching.rdms.domain.Account;
import com.github.ggreen.caching.rdms.geode.AccountGeodeRepository;
import nyla.solutions.core.patterns.batch.BatchReport;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.patterns.jdbc.batch.SelectResultSetConverterSupplier;
import nyla.solutions.core.util.Scheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountDbBatchTest
{
    private AccountGeodeRepository geodeRepository;

    @BeforeEach
    void setUp()
    {
        geodeRepository = mock(AccountGeodeRepository.class);
    }

    @Nested
    class WhenConstructCdcSql
    {

        @Test
        void constructCdcSql()
        {
            long expectedMaxId = 23L;
            int year = 1950;
            int month = 12;
            int day = 14;
            int hour = 7;
            int minute = 40;
            int second = 12;
            int milliseconds = 10;
            long expectedMaxTime = Scheduler.toDate(LocalDateTime.of(year,month,day,hour,minute,second,milliseconds)).getTime();
            AccountGeodeRepository geodeRepository = mock(AccountGeodeRepository.class);
            Long[] expectedResults = {expectedMaxId,expectedMaxTime};
            when(geodeRepository.selectMaxAccountIdAndTimestamp()).thenReturn(expectedResults);
            String sql = AccountDbBatch.constructCdcSql(geodeRepository);

            System.out.println(sql);
            assertNotNull(sql);

            assertThat(sql).contains(" WHERE ");
            assertThat(sql).contains("TIMESTAMP");
            assertThat(sql).contains("DATE_TRUNC");

            assertThat(sql).contains(Long.toString(expectedMaxId));
            assertThat(sql).contains(String.valueOf(year));
            assertThat(sql).contains(String.valueOf(month));
            assertThat(sql).contains(String.valueOf(day));
            assertThat(sql).contains(String.valueOf(hour));
            assertThat(sql).contains(String.valueOf(minute));
            assertThat(sql).contains(String.valueOf(second));
            assertThat(sql).contains(" OR ");
           // assertThat(sql).contains(String.valueOf(milliseconds));

        }
        @Test
        void constructCdcSql_NoMax_ReturnSql()
        {
            Long expectedMaxId = null;
            Long expectedMaxTime = null;
            AccountGeodeRepository geodeRepository = mock(AccountGeodeRepository.class);
            Long[] expectedResults = {expectedMaxId,expectedMaxTime};
            when(geodeRepository.selectMaxAccountIdAndTimestamp()).thenReturn(expectedResults);
            String sql = AccountDbBatch.constructCdcSql(geodeRepository);
            assertNotNull(sql);

            assertThat(sql).doesNotContain(" WHERE ");


        }
        @Test
        void constructCdcSql_Null_ReturnSql()
        {
            AccountGeodeRepository geodeRepository = mock(AccountGeodeRepository.class);
            String sql = AccountDbBatch.constructCdcSql(geodeRepository);
            assertNotNull(sql);
            assertThat(sql).contains("ACCOUNT");
        }
        @Test
        void constructCdcSql_OnlyMaxTime()
        {
            Long expectedMaxId = null;
            long expectedMaxTime = System.currentTimeMillis();

            Long[] expectedResults = {expectedMaxId,expectedMaxTime};
            when(geodeRepository.selectMaxAccountIdAndTimestamp()).thenReturn(expectedResults);
            String sql = AccountDbBatch.constructCdcSql(geodeRepository);
            assertNotNull(sql);

            assertThat(sql).contains(" WHERE ");
            assertThat(sql).doesNotContain("ACCOUNT_ID");
            assertThat(sql).contains("ACCOUNT_TIMESTAMP");

        }

        @Test
        void constructCdcSql_onlyMaxId()
        {
            long expectedMaxId = 23L;
            Long expectedMaxTime = null;
            AccountGeodeRepository geodeRepository = mock(AccountGeodeRepository.class);
            Long[] expectedResults = {expectedMaxId,expectedMaxTime};
            when(geodeRepository.selectMaxAccountIdAndTimestamp()).thenReturn(expectedResults);
            String sql = AccountDbBatch.constructCdcSql(geodeRepository);
            assertNotNull(sql);
            assertThat(sql).contains(" WHERE ");
            assertThat(sql).contains(Long.toString(expectedMaxId));

        }
    }


    @Test
    void execute() throws SQLException
    {
        Account expectedAccount = new JavaBeanGeneratorCreator<>(Account.class).randomizeAll().create();
        Consumer consumer = mock(Consumer.class);
        SelectResultSetConverterSupplier<Account> supplier = mock(SelectResultSetConverterSupplier.class);
        when(supplier.get()).thenReturn(expectedAccount).thenReturn(null);
        AccountDbBatch subject = new AccountDbBatch(supplier,consumer,geodeRepository);
        BatchReport report = subject.execute();
        verify(supplier).setSql(anyString());
        verify(consumer).accept(any());
        assertNotNull(report);

    }
}