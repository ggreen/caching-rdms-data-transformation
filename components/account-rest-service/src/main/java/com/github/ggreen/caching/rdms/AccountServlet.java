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
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        Long accountId = accountId(request);
        Account account = repository.findById(accountId);
        if(account == null)
        {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        else
        {
            response.getWriter().write(accountToJson.apply(account));
        }


    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        Account account = this.toAccount(req);
        if(account == null)
            throw new NullPointerException("Account JSON required in HTTP POST body");

        account = repository.update(account);
        resp.getWriter().write(accountToJson.apply(account));
    }


    public void doDelete(HttpServletRequest request, HttpServletResponse resp) throws ServletException
    {
        Long accountId = this.accountId(request);
        if(accountId == null)
            throw new NullPointerException("Account Id not found in URL:"+request.getRequestURI());
        boolean found = this.repository.deleteAccountById(accountId);

        if(!found)
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        else
            resp.setStatus(HttpServletResponse.SC_OK);

    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        Account account = toAccount(request);
        this.repository.create(account);
    }

    protected Account toAccount(HttpServletRequest request) throws IOException
    {
        String json = IO.readText(request.getReader());
        Account account = this.jsonToAccount.apply(json);
        return account;
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