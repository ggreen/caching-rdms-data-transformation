package com.github.ggreen.caching.rdms;

import com.github.ggreen.caching.rdms.domain.AccountToJson;
import com.github.ggreen.caching.rdms.domain.JsonToAccount;
import com.github.ggreen.caching.rdms.domain.jdbc.AccountJdbcRepository;
import com.github.ggreen.caching.rdms.domain.jdbc.ApacheDbcpConnections;

public class AccountDbServlet extends AccountServlet
{
    public AccountDbServlet()
    {
        super(new AccountJdbcRepository(new ApacheDbcpConnections()),
                new AccountToJson(), new JsonToAccount());

    }
}
