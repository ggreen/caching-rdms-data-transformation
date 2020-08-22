package com.github.ggreen.caching.rdms;

import com.github.ggreen.caching.rdms.domain.AccountToJson;
import com.github.ggreen.caching.rdms.domain.JsonToAccount;
import com.github.ggreen.caching.rdms.domain.jdbc.AccountGeodeRepository;

public class AccountCacheServlet extends AccountServlet
{
    public AccountCacheServlet()
    {
        super(new AccountGeodeRepository(),
                new AccountToJson(), new JsonToAccount());

    }
}
