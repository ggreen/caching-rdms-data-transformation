package com.github.ggreen.caching.rdms.geode;

import com.github.ggreen.caching.rdms.domain.Account;
import com.github.ggreen.caching.rdms.geode.AccountGeodeRepository;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.apache.geode.cache.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AccountGeodeRepositoryTest
{
    Region<Long,Account> region = mock(Region.class);
    private AccountGeodeRepository subject;
    private Account expected;

    @BeforeEach
    void setUp()
    {
        region = mock(Region.class);
        subject = new AccountGeodeRepository(region);
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
    void save()
    {
        Account actual = subject.save(expected);
        assertEquals(expected,actual);
        verify(region).put(actual.getId(),actual);
    }

    @Test
    void deleteAccountById_When_found_returns_true()
    {
        Long expectedAccountId = 3L;
        boolean actual = subject.deleteAccountById(expectedAccountId);
        assertTrue(actual);
        verify(region).remove(expectedAccountId);
    }
}