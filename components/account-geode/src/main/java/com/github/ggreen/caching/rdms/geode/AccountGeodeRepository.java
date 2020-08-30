package com.github.ggreen.caching.rdms.geode;

import com.github.ggreen.caching.rdms.domain.Account;
import com.github.ggreen.caching.rdms.domain.AccountRepository;
import io.pivotal.services.dataTx.geode.client.GeodeClient;
import nyla.solutions.core.util.Debugger;
import org.apache.geode.cache.Region;

/**
 * @author Gregory Green
 */
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
        Debugger.println(this,"create account:"+account.getId());
        this.accountRegion.create(account.getId(),account);
        return account;
    }

    @Override
    public Account findById(Long accountId)
    {
        Debugger.println(this,"read account:"+accountId);
        return accountRegion.get(accountId);
    }

    @Override
    public Account update(Account account)
    {
        Debugger.println(this,"update account:"+account.getId());
        accountRegion.put(account.getId(),account);
        return account;
    }

    @Override
    public boolean deleteAccountById(Long accountId)
    {
        Debugger.println(this,"delete account:"+accountId);
        accountRegion.remove(accountId);
        return true;
    }

    @Override
    public Account save(Account account)
    {
        return update(account);
    }
}
