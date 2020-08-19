package com.github.ggreen.caching.rdms;


import com.github.ggreen.caching.rdms.domain.Account;
import com.github.ggreen.caching.rdms.domain.AccountJdbcRepository;
import nyla.solutions.core.patterns.jdbc.Sql;

import java.io.IOException;
import java.sql.SQLException;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AccountDbServlet extends HttpServlet
{
    private final AccountJdbcRepository repository;

    public AccountDbServlet(AccountJdbcRepository repository)
    {
        this.repository = repository;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        Long accountId = accountId(req);
        try {
            Account account = repository.findById(accountId);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

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