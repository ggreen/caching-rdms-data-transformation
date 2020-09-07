# caching-rdms-data-transformation


This project has been developed in support of the 
[ApacheCon 2020](https://apachecon.com/acna2020/).
This project demonstrates a test driven development approach 
to building data domains where the source system of record is a 
legacy Relation Database Management System. 

The initial code will focus on mainframe based DB2 migration. 
The pattern will be applicable to similar solutions using Oracle,
 Sybase and other similar traditional relational databases. 
 The talk will highlight the pros and cons of different styles of data pipelines. 
 For example, Day 0 initial loads, Change Data Capture, Event based streams and 
 scheduled pulled based tasks. 
 
 
 The following are the highlighted technologies;
 
 - [Apache Geode](https://geode.apache.org/)
 - [Apache Kafka](https://kafka.apache.org/) 
 


This project contains several cloud native based Java applications.


Application                                                                     | Notes
----------------------------------------------------                            | ---------------------------------------------
[account-db-rest-service](applications/account-db-rest-service)                 | REST Data Service that supports JDBC and or Apache Geode
[account-db-cache-batch-pipeline](applications/account-db-cache-batch-pipeline) | Domain Data pipeline to load Apache Geode
[account-db-sink-stream-pipeline](applications/account-db-sink-stream-pipeline) | Apache Kafka application to publish data from JDBC to a queue
[applications/account-db-source-stream-pipeline](applications/account-db-source-stream-pipeline) | Apache Kafka application to read data from a queue to publish to Apache Geode 
[account-db-migrations](applications/account-db-migrations)                     | Flyway based database migration


The following are common environment variable configuration properties.


```shell script
export FACTORY_REPOSITORY=com.github.ggreen.caching.rdms.jdbc.AccountJdbcRepository
export FACTORY_REPOSITORY=com.github.ggreen.caching.rdms.geode.AccountGeodeRepository
export FACTORY_REPOSITORY=com.github.ggreen.caching.rdms.lookAside.AccountGeodeJdbcRepository
export CRYPTION_KEY=APACHECON
export JDBC_URL=jdbc:db2://localhost:50000/testdb
export JDBC_DRIVER_CLASS_NAME=com.ibm.db2.jcc.DB2Driver
export JDBC_USERNAME=db2inst1
export JDBC_PASSWORD={cryption}yHFQacZf7JcIwV4zAW9Xog==
```




# Running Service

## JDBC

```shell script
export FACTORY_REPOSITORY=com.github.ggreen.caching.rdms.jdbc.AccountJdbcRepository
java -jar applications/account-db-rest-service/target/account-db-rest-service-1.0-SNAPSHOT.jar 
```

## Apache Geode

```shell script
export FACTORY_REPOSITORY=com.github.ggreen.caching.rdms.geode.AccountGeodeRepository
java -jar applications/account-db-rest-service/target/account-db-rest-service-1.0-SNAPSHOT.jar 
```

## JDBC/Apache Geode Look Aside Cache
```shell script
export FACTORY_REPOSITORY=com.github.ggreen.caching.rdms.geode.AccountGeodeRepository
java -jar applications/account-db-rest-service/target/account-db-rest-service-1.0-SNAPSHOT.jar 
```


# Data Pipeline

## Batch 
```shell script
export CRYPTION_KEY=APACHECON
export JDBC_URL=jdbc:db2://localhost:50000/testdb
export JDBC_DRIVER_CLASS_NAME=com.ibm.db2.jcc.DB2Driver
export JDBC_USERNAME=db2inst1
export JDBC_PASSWORD={cryption}yHFQacZf7JcIwV4zAW9Xog==

java -jar applications/account-db-cache-batch-pipeline/target/account-db-cache-batch-pipeline-1.0-SNAPSHOT.jar 

```


## Stream

### Account Source

```shell script
export KAFKA_APPLICATION_ID_CONFIG=applications-source

java -jar applications/account-db-source-stream-pipeline/target/account-db-source-stream-pipeline-1.0-SNAPSHOT.jar
```


### Account Sink

```shell script
export KAFKA_APPLICATION_ID_CONFIG=applications-sink
java  -jar applications/account-db-sink-stream-pipeline/target/account-db-sink-stream-pipeline-1.0-SNAPSHOT.jar

```


----------------

# JDBC/DB2 migrations

Start the JDBC migration.


```shell script

java -jar applications/account-db-rest-service/target/account-db-migrations-1.0-SNAPSHOT.jar 
