package com.github.ggreen.caching.rdms.pipeline;

import nyla.solutions.core.util.Debugger;

import java.sql.SQLException;

public class AccountDbSourceApp
{
    public static void main(String[] args) throws SQLException, InterruptedException
    {
        try {
            Thread thread = new AccountDbSource().start();

            thread.join();
        }
        catch (SQLException | InterruptedException e) {
            Debugger.printFatal(e);
            throw e;
        }
     }
}
