package com.github.ggreen.caching.rdms.pipeline;

import com.github.ggreen.caching.rdms.domain.Account;
import nyla.solutions.core.patterns.batch.BatchReport;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.patterns.jdbc.batch.SelectResultSetConverterSupplier;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountCacheBatchAppTest
{

    @Test
    void runBatch() throws SQLException
    {
        Account expectedAccound = new JavaBeanGeneratorCreator<>(Account.class).randomizeAll().create();
        Consumer<List<Account>> geodeConsumer = mock(Consumer.class);
        BatchReport expected = new BatchReport();
        SelectResultSetConverterSupplier<Account> supplier = mock(SelectResultSetConverterSupplier.class);
        when(supplier.get()).thenReturn(expectedAccound).thenReturn(null);
        AccountCacheBatchApp subject = new AccountCacheBatchApp(supplier,geodeConsumer);
        BatchReport report = subject.execute();
        verify(geodeConsumer).accept(any());
        assertNotNull(report);

    }
}