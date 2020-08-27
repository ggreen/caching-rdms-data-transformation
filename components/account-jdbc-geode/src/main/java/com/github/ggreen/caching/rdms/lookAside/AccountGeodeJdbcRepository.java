package com.github.ggreen.caching.rdms.lookAside;

import com.github.ggreen.caching.rdms.domain.Account;
import com.github.ggreen.caching.rdms.domain.AccountRepository;
import com.github.ggreen.caching.rdms.geode.AccountGeodeRepository;
import com.github.ggreen.caching.rdms.jdbc.AccountJdbcRepository;

/**
 * @author Gregory Green
 */
public class AccountGeodeJdbcRepository implements AccountRepository
{
    private final AccountGeodeRepository geodeRepository;
    private final AccountJdbcRepository jdbcRepository;

    public AccountGeodeJdbcRepository()
    {
        this(new AccountGeodeRepository(),new AccountJdbcRepository());
    }
    public AccountGeodeJdbcRepository(AccountGeodeRepository geodeRepository, AccountJdbcRepository jdbcRepository)
    {
        this.geodeRepository = geodeRepository;
        this.jdbcRepository = jdbcRepository;
    }

    @Override
    public Account create(Account account)
    {
        return jdbcRepository.create(account);
    }

    @Override
    public Account findById(Long accountId)
    {
        Account account = geodeRepository.findById(accountId);
        if(account == null){
            account = jdbcRepository.findById(accountId);

            if(account != null)
            {
                //cache
                geodeRepository.save(account);
            }
        }

        return account;
    }

    @Override
    public Account update(Account account)
    {
        evictAccountCache(account);
        Account updated = jdbcRepository.update(account);

        return updated;
    }

    @Override
    public boolean deleteAccountById(Long accountId)
    {
        evictAccountCache(accountId);
        return jdbcRepository.deleteAccountById(accountId);
    }

    @Override
    public Account save(Account account)
    {
        evictAccountCache(account);
        return jdbcRepository.save(account);
    }

    private void evictAccountCache(Account account)
    {
        evictAccountCache(account.getId());
    }

    private void evictAccountCache(Long id)
    {
        geodeRepository.deleteAccountById(id);
    }
}
