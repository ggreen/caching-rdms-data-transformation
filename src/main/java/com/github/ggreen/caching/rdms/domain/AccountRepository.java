package com.github.ggreen.caching.rdms.domain;

import java.sql.SQLException;

public interface AccountRepository
{
    public void create(Account account);

    public Account findById(Long accountId);

    public void update(Account account);

    public void deleteAccountById(Long accountId);
}
