# caching-rdms-data-transformation


Example settings

```properties
jdbc.url=jdbc:h2:mem:test
jdbc.driver.class.name=org.h2.Driver
jdbc.username=sa
jdbc.password=sa
```

export CRYPTION_KEY=APACHECON

## Geode Testing

```shell script

export SERVLET_CLASS_NAME=com.github.ggreen.caching.rdms.AccountCacheServlet

```

## DB2 Testing

user: db2inst1
DBNAME=testdb
security


export SERVLET_CLASS_NAME=com.github.ggreen.caching.rdms.AccountDbServlet
export JDBC_URL=jdbc:db2://localhost:50000/testdb
export JDBC_DRIVER_CLASS_NAME=com.ibm.db2.jcc.DB2Driver
export JDBC_USERNAME=db2inst1
export JDBC_PASSWORD={cryption}yHFQacZf7JcIwV4zAW9Xog==

## Migration

### JDBC/DB2

export baseline=true

java -jar applications/account-db-migrations/target/account-db-migrations-1.0-SNAPSHOT.jar 

## Data Pipeline

java -jar applications/account-db-cache-batch-pipeline/target/account-db-cache-batch-pipeline-1.0-SNAPSHOT.jar 


# Running Service

java -jar applications/account-db-data-service/target/account-db-data-service-1.0-SNAPSHOT.jar 
