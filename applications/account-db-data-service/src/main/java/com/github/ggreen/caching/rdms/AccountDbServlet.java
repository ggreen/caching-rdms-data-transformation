package com.github.ggreen.caching.rdms;


import com.github.ggreen.caching.rdms.domain.Account;
import com.github.ggreen.caching.rdms.domain.jdbc.AccountJdbcRepository;
import com.github.ggreen.caching.rdms.domain.AccountRepository;
import com.github.ggreen.caching.rdms.domain.AccountToJson;

import java.io.IOException;
import java.util.function.Function;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AccountDbServlet extends HttpServlet
{
    private final AccountRepository repository;
    private final Function<Account,String> converter;

    public AccountDbServlet()
    {
        this(new AccountJdbcRepository(new ApacheDbcpConnections()),
                new AccountToJson());
    }
    public AccountDbServlet(AccountRepository repository, Function<Account, String> converter)
    {
        this.repository = repository;
        this.converter = converter;
    }
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        Long accountId = accountId(req);
        Account account = repository.findById(accountId);
        resp.getWriter().write(converter.apply(account));
    }

    protected Long accountId(HttpServletRequest req)
    {
        String url = req.getRequestURI();
        if(url == null)
            return null;

        int lastSlashIndex = url.lastIndexOf("/");
        if(lastSlashIndex < 0)
            return null;

        String id = url.substring(lastSlashIndex+1);
        if(id.length() == 0)
            return null;

        return Long.valueOf(id);
    }
}