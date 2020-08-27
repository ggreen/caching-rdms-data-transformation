package com.github.ggreen.caching.rdms.lookAside;

import com.github.ggreen.caching.rdms.domain.Account;
import com.github.ggreen.caching.rdms.geode.AccountGeodeRepository;
import com.github.ggreen.caching.rdms.jdbc.AccountJdbcRepository;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class AccountGeodeJdbcRepositoryTest
{
    private AccountGeodeJdbcRepository subject;
    private AccountJdbcRepository jdbcRepository;
    private AccountGeodeRepository geodeRepository;
    private Account account;

    @BeforeEach
    void setUp()
    {
        jdbcRepository = mock(AccountJdbcRepository.class);
        geodeRepository = mock(AccountGeodeRepository.class);
        subject = new AccountGeodeJdbcRepository(geodeRepository,jdbcRepository);
        account = new JavaBeanGeneratorCreator<>(Account.class)
                .randomizeAll()
                .create();
    }

    @Test
    void create()
    {
        subject.create(account);
        verify(jdbcRepository).create(account);
    }

    @Test
    void findById()
    {
        subject.findById(account.getId());
        verify(geodeRepository).findById(account.getId());
        verify(jdbcRepository).findById(account.getId());
    }

    @Test
    void findById_when_found_save()
    {
        when(jdbcRepository.findById(anyLong())).thenReturn(account);
        subject.findById(account.getId());
        verify(geodeRepository).findById(account.getId());

        verify(jdbcRepository).findById(account.getId());
        verify(geodeRepository).save(account);

    }


    @Test
    void update()
    {
        subject.update(account);
        verify(geodeRepository).deleteAccountById(account.getId());
        verify(jdbcRepository).update(account);
    }

    @Test
    void deleteAccountById()
    {
        subject.deleteAccountById(account.getId());
        verify(geodeRepository).deleteAccountById(account.getId());
        verify(jdbcRepository).deleteAccountById(account.getId());
    }

    @Test
    void save()
    {
        subject.save(account);
        verify(geodeRepository).deleteAccountById(account.getId());
        verify(jdbcRepository).save(account);
    }
}