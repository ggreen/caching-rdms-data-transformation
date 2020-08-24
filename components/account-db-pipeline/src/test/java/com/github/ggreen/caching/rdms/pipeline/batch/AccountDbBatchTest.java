package com.github.ggreen.caching.rdms.pipeline.batch;

import com.github.ggreen.caching.rdms.domain.Account;
import com.github.ggreen.caching.rdms.pipeline.batch.AccountDbBatch;
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

class AccountDbBatchTest
{

    @Test
    void runBatch() throws SQLException
    {
        Account expectedAccound = new JavaBeanGeneratorCreator<>(Account.class).randomizeAll().create();
        Consumer<List<Account>> consumer = mock(Consumer.class);
        BatchReport expected = new BatchReport();
        SelectResultSetConverterSupplier<Account> supplier = mock(SelectResultSetConverterSupplier.class);
        when(supplier.get()).thenReturn(expectedAccound).thenReturn(null);
        AccountDbBatch subject = new AccountDbBatch(supplier,consumer);
        BatchReport report = subject.execute();
        verify(consumer).accept(any());
        assertNotNull(report);

    }
}