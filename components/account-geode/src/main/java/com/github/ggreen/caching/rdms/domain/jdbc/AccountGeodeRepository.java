package com.github.ggreen.caching.rdms.domain.jdbc;

import com.github.ggreen.caching.rdms.domain.Account;
import com.github.ggreen.caching.rdms.domain.AccountRepository;
import io.pivotal.services.dataTx.geode.client.GeodeClient;
import org.apache.geode.cache.Region;

public class AccountGeodeRepository implements AccountRepository
{
    private final Region<Long, Account> accountRegion;
    public AccountGeodeRepository()
    {
        try {
            accountRegion = GeodeClient.connect().getRegion("accounts");
        }
        catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public AccountGeodeRepository(Region<Long, Account> accountRegion)
    {
        this.accountRegion = accountRegion;
    }

    @Override
    public Account create(Account account)
    {
        this.accountRegion.create(account.getId(),account);
        return account;
    }

    @Override
    public Account findById(Long accountId)
    {
        return accountRegion.get(accountId);
    }

    @Override
    public Account update(Account account)
    {
        accountRegion.put(account.getId(),account);
        return account;
    }

    @Override
    public boolean deleteAccountById(Long accountId)
    {
        accountRegion.remove(accountId);
        return true;

    }
}
