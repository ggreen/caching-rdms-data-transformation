package com.github.ggreen.caching.rdms.migration;

import nyla.solutions.core.patterns.jdbc.Sql;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

class AccountMigrationAppTest
{
    @Test
    void main() throws SQLException
    {
        String driver = "org.h2.Driver";

        String url = "jdbc:h2:file:./target/accountDB";
        String user = "sa";
        String password = "password";
        String args[] = {"--username="+user,"--driver="+driver,"--url="+url,"--password="+password};
        AccountMigrationApp.main(args);

        Sql sql = new Sql();

        try(Connection connection =Sql.createConnection(driver,url,user,password.toCharArray()))
        {
            String select = "select * from app.account";
            Map<String,?> row = sql.queryForMap(connection,select);
        }
    }
}