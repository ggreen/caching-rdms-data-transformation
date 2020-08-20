package com.github.ggreen.caching.rdms.migration;

import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.settings.Settings;
import org.flywaydb.core.Flyway;

public class AccountMigrationApp
{

    public static void main(String[] args)
    {
        Settings settings = Config.loadArgs(args);


        //String args[] = {"--username="+user,"--driver="+driver,"--url="+url,"--password="+password};
        // Create the Flyway instance and point it to the database
        Flyway flyway = Flyway.configure()

                              .dataSource(
                                      settings.getProperty("url"),
                                      settings.getProperty("username"),
                                      settings.getProperty("password"))

                              .load();

        // Start the migration
        flyway.migrate();
    }
}
