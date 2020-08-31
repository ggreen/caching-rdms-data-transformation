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



## DB2 Testing

user: db2inst1
DBNAME=testdb
security


export FACTORY_REPOSITORY=com.github.ggreen.caching.rdms.jdbc.AccountJdbcRepository
export FACTORY_REPOSITORY=com.github.ggreen.caching.rdms.geode.AccountGeodeRepository
export FACTORY_REPOSITORY=com.github.ggreen.caching.rdms.lookAside.AccountGeodeJdbcRepository

export CRYPTION_KEY=APACHECON
export JDBC_URL=jdbc:db2://localhost:50000/testdb
export JDBC_DRIVER_CLASS_NAME=com.ibm.db2.jcc.DB2Driver
export JDBC_USERNAME=db2inst1
export JDBC_PASSWORD={cryption}yHFQacZf7JcIwV4zAW9Xog==

## Migration

### JDBC/DB2


java -jar applications/account-db-migrations/target/account-db-migrations-1.0-SNAPSHOT.jar 

## Data Pipeline

java -jar applications/account-db-cache-batch-pipeline/target/account-db-cache-batch-pipeline-1.0-SNAPSHOT.jar 


# Running Service

java -jar applications/account-db-data-service/target/account-db-data-service-1.0-SNAPSHOT.jar 


# Account Sink

export KAFKA_APPLICATION_ID_CONFIG=applications-sink
