package com.github.ggreen.caching.rdms.migration;

import nyla.solutions.core.patterns.jdbc.Sql;
import nyla.solutions.core.util.Config;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

class AccountDbMigrationAppTest
{
    @Test
    void main() throws SQLException
    {
        String driver = "org.h2.Driver";

        String jdbcUrl = "jdbc:h2:file:./target/accountDB";
        System.setProperty("JDBC_URL", jdbcUrl);
        String userName = "sa";
        System.setProperty("JDBC_USERNAME", userName);
        String password = "password";
        System.setProperty("JDBC_PASSWORD", password);
        Config.reLoad();
        String[] args = {};
        AccountDbMigrationApp.main(args);

        Sql sql = new Sql();

        try(Connection connection =Sql.createConnection(driver,jdbcUrl,userName,password.toCharArray()))
        {
            String select = "select * from app.account";
            Map<String,?> row = sql.queryForMap(connection,select);
        }
    }
}