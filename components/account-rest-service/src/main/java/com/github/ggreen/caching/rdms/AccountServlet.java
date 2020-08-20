package com.github.ggreen.caching.rdms;

import com.github.ggreen.caching.rdms.domain.Account;
import com.github.ggreen.caching.rdms.domain.AccountRepository;
import nyla.solutions.core.io.IO;
import java.io.IOException;
import java.util.function.Function;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AccountServlet extends HttpServlet
{
    private final AccountRepository repository;
    private final Function<Account,String> accountToJson;
    private final Function<String, Account> jsonToAccount;

    public AccountServlet(AccountRepository repository, Function<Account, String> accountToJson, Function<String,
            Account> jsonToAccount)
    {
        this.repository = repository;
        this.accountToJson = accountToJson;
        this.jsonToAccount = jsonToAccount;
    }
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        Long accountId = accountId(req);
        Account account = repository.findById(accountId);
        resp.getWriter().write(accountToJson.apply(account));
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String json = IO.readText(request.getReader());
        Account account = this.jsonToAccount.apply(json);
        this.repository.create(account);
    }

    protected Long accountId(HttpServletRequest request)
    {
        String url = request.getRequestURI();
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