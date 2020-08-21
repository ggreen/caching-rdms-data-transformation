package com.github.ggreen.caching.rdms.domain;

import java.sql.SQLException;

public interface AccountRepository
{
    public Account create(Account account);

    public Account findById(Long accountId);

    public Account update(Account account);

    public boolean deleteAccountById(Long accountId);
}
