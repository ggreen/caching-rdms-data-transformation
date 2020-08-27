package com.github.ggreen.caching.rdms.migration;

import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.settings.Settings;
import org.flywaydb.core.Flyway;

/**
 * @author Gregory Green
 */
public class AccountDbMigrationApp
{

    // Whether to automatically call baseline when migrate is executed against a non-empty schema wi
    private static final String BASELINE_ON_PROP = "baseline";

    public static void main(String[] args)
    {
        Settings settings = Config.loadArgs(args);


        //String args[] = {"--username="+user,"--driver="+driver,"--url="+url,"--password="+password};
        // Create the Flyway instance and point it to the database
        Flyway flyway = Flyway.configure()
                              .baselineOnMigrate(Config.getPropertyBoolean(BASELINE_ON_PROP,false))
                              .dataSource(
                                      settings.getProperty("JDBC_URL"),
                                      settings.getProperty("JDBC_USERNAME"),
                                      settings.getProperty("JDBC_PASSWORD"))

                              .load();

        // Start the migration
        flyway.migrate();
    }
}
