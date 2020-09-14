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
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountDbBatchTest
{
    private AccountGeodeRepository geodeRepository;
    private Consumer<List<Account>> consumer;
    private SelectResultSetConverterSupplier<Account> supplier ;
    private AccountDbBatch subject;

    @BeforeEach
    void setUp()
    {
        geodeRepository = mock(AccountGeodeRepository.class);
        consumer = mock(Consumer.class);
        supplier = mock(SelectResultSetConverterSupplier.class);
        subject = new AccountDbBatch(supplier,consumer,geodeRepository);

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
            Long[] expectedResults = {expectedMaxId,expectedMaxTime};
            when(geodeRepository.selectMaxAccountIdAndTimestamp()).thenReturn(expectedResults);
            subject.constructCdcSupplier();

            String actualSql = subject.getSql();
            System.out.println(actualSql);
            assertNotNull(actualSql);

            assertThat(actualSql).contains(" WHERE ");
            assertThat(actualSql).contains("?");


        }
        @Test
        void constructCdcSql_NoMax_ReturnSql()
        {
            Long expectedMaxId = null;
            Long expectedMaxTime = null;

            Long[] expectedResults = {expectedMaxId,expectedMaxTime};
            when(geodeRepository.selectMaxAccountIdAndTimestamp()).thenReturn(expectedResults);
            subject.constructCdcSupplier();
            String actualSql = subject.getSql();

            System.out.println(actualSql);
            assertNotNull(actualSql);

            assertThat(actualSql).doesNotContain(" WHERE ");


        }
        @Test
        void constructCdcSql_Null_ReturnSql()
        {
            AccountGeodeRepository geodeRepository = mock(AccountGeodeRepository.class);
            subject.constructCdcSupplier();
            String sql = subject.getSql();
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
            subject.constructCdcSupplier();
            String sql = subject.getSql();
            assertNotNull(sql);

            assertThat(sql).contains(" WHERE ");
            assertThat(sql).doesNotContain("ACCOUNT_ID");
            assertThat(sql).contains("ACCOUNT_TIMESTAMP");
            verify(supplier).setParameters(any());

        }

        @Test
        void constructCdcSql_onlyMaxId()
        {
            long expectedMaxId = 23L;
            Long expectedMaxTime = null;
            Long[] expectedResults = {expectedMaxId,expectedMaxTime};
            when(geodeRepository.selectMaxAccountIdAndTimestamp()).thenReturn(expectedResults);
            subject.constructCdcSupplier();
            String sql = subject.getSql();
            assertNotNull(sql);
            assertThat(sql).contains(" WHERE ");
            assertThat(sql).contains("?");
            verify(supplier).setParameters(any());

        }
    }


    @Test
    void execute() throws SQLException
    {
        Account expectedAccount = new JavaBeanGeneratorCreator<>(Account.class).randomizeAll().create();
        when(supplier.get()).thenReturn(expectedAccount).thenReturn(null);

        BatchReport report = subject.execute();
        verify(supplier,atLeastOnce()).setSql(anyString());
        verify(consumer).accept(any());
        assertNotNull(report);

    }
}