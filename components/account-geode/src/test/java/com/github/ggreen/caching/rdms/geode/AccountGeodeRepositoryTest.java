package com.github.ggreen.caching.rdms.geode;

import com.github.ggreen.caching.rdms.domain.Account;
import io.pivotal.services.dataTx.geode.io.QuerierService;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.query.Struct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AccountGeodeRepositoryTest
{
    private Region<Long,Account> region;
    private QuerierService queryService;
    private AccountGeodeRepository subject;
    private Account expected;

    @BeforeEach
    void setUp()
    {
        region = mock(Region.class);
        queryService = mock(QuerierService.class);
        subject = new AccountGeodeRepository(region,queryService);
        expected = new JavaBeanGeneratorCreator<>(Account.class)
                .randomizeAll().create();
    }

    @Test
    void create()
    {
        Account actual = subject.create(expected);
        assertEquals(expected,actual);
        verify(region).create(actual.getId(),actual);

    }
    @Test
    void create_hasCurrentTimestamp()
    {
        Account expected = Account.builder().id(2L)
                .name("test").build();
        Account actual = subject.create(expected);
        assertNotNull(actual.getCurrentTimestamp());

    }

    @Test
    void create_throwsExceptionWhenIdNotGiven()
    {
        Account expected = new Account();
        assertThrows(IllegalArgumentException.class, () -> subject.create(expected));
    }

    @Test
    void findById()
    {
        Long expectedId = 1L;
        subject.findById(expectedId);
        verify(region).get(expectedId);
    }

    @Test
    void update()
    {

        Account actual = subject.update(expected);
        assertEquals(expected,actual);
        verify(region).put(actual.getId(),actual);
    }
    @Test
    void update_hasCurrentTimestamp()
    {
        Account expected = Account.builder().id(2L)
                                  .name("test").build();
        Account actual = subject.update(expected);
        assertNotNull(actual.getCurrentTimestamp());

    }

    @Test
    void update_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class,
                () ->subject.update(new Account()));
    }
    @Test
    void save()
    {
        Account actual = subject.save(expected);
        assertEquals(expected,actual);
        verify(region).put(actual.getId(),actual);
    }

    @Test
    void save_hasCurrentTimestamp()
    {
        Account expected = Account.builder().id(2L)
                                  .name("test").build();
        Account actual = subject.save(expected);
        assertNotNull(actual.getCurrentTimestamp());

    }

    @Test
    void deleteAccountById_When_found_returns_true()
    {
        Long expectedAccountId = 3L;
        boolean actual = subject.deleteAccountById(expectedAccountId);
        assertTrue(actual);
        verify(region).remove(expectedAccountId);
    }

    @Nested
    public class WhenSelectMaxAccountIdAndTimestamp
    { ;

        @Test
        void selectMaxAccountIdAndTimestamp()
        {
            long expectedMaxAccountId = Integer.MAX_VALUE/2;
            long expectedMaxTimestamp = System.currentTimeMillis();
            Struct expectedIdAndTimestamp = mock(Struct.class);
            when(expectedIdAndTimestamp.get("id")).thenReturn(expectedMaxAccountId);
            when(expectedIdAndTimestamp.get("currentTimestamp")).thenReturn(expectedMaxTimestamp);
            Collection<Object> expectedResults = Arrays.asList(expectedIdAndTimestamp);
            when(queryService.query(anyString())).thenReturn(expectedResults);

            Long[] longs = subject.selectMaxAccountIdAndTimestamp();
            verify(queryService).query(anyString());
            assertNotNull(longs);
            assertEquals(expectedMaxAccountId,longs[0]);
            assertEquals(expectedMaxTimestamp,longs[1]);

        }

        @Test
        void selectMaxAccountIdAndTimestamp_returnNullTimestamp()
        {
            Long expectedMaxAccountId = Long.valueOf(2);
            Long expectedMaxTimestamp = null;
            Struct expectedIdAndTimestamp = mock(Struct.class);
            when(expectedIdAndTimestamp.get("id")).thenReturn(expectedMaxAccountId);
            when(expectedIdAndTimestamp.get("currentTimestamp")).thenReturn(expectedMaxTimestamp);
            Collection<Object> expectedResults = Arrays.asList(expectedIdAndTimestamp);
            when(queryService.query(anyString())).thenReturn(expectedResults);

            Long[] longs = subject.selectMaxAccountIdAndTimestamp();
            verify(queryService).query(anyString());
            assertNotNull(longs);
            assertEquals(expectedMaxAccountId,longs[0]);
            assertEquals(expectedMaxTimestamp,longs[1]);

        }

        @Test
        void selectMaxAccountIdAndTimestamp_returnNullAccountId()
        {
            Long expectedMaxAccountId = null;
            Long expectedMaxTimestamp = System.currentTimeMillis();
            Struct expectedIdAndTimestamp = mock(Struct.class);
            when(expectedIdAndTimestamp.get("id")).thenReturn(expectedMaxAccountId);
            when(expectedIdAndTimestamp.get("currentTimestamp")).thenReturn(expectedMaxTimestamp);
            Collection<Object> expectedResults = Arrays.asList(expectedIdAndTimestamp);
            when(queryService.query(anyString())).thenReturn(expectedResults);

            Long[] longs = subject.selectMaxAccountIdAndTimestamp();
            verify(queryService).query(anyString());
            assertNotNull(longs);
            assertEquals(expectedMaxAccountId,longs[0]);
            assertEquals(expectedMaxTimestamp,longs[1]);
        }

        @Test
        void selectMaxAccountIdAndTimestamp_returnsNull()
        {
            Long[] longs = subject.selectMaxAccountIdAndTimestamp();
            verify(queryService).query(anyString());
            assertNull(longs);

        }
    }

}