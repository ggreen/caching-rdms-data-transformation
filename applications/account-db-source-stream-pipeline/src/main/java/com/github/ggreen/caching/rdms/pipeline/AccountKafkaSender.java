package com.github.ggreen.caching.rdms.pipeline;

import com.github.ggreen.caching.rdms.domain.Account;
import com.github.ggreen.caching.rdms.domain.AccountToJson;
import nyla.solutions.core.util.Config;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

public class AccountKafkaSender implements Consumer<List<Account>>, AutoCloseable
{
    private final Producer<String, String> producer;
    private final AccountToJson accountToJson;
    private final String topic;

    public AccountKafkaSender()
    {
        Properties props = new Properties();
        props.put("bootstrap.servers", Config.getProperty("KAFKA_BOOTSTRAP_SERVERS"));
        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        this.producer = new KafkaProducer<>(props);;
        this.accountToJson = new AccountToJson();
        this.topic = Config.getProperty("KAFKA_TOPIC");
    }

    public AccountKafkaSender(Producer<String, String> producer, AccountToJson accountToJson, String topic)
    {
        this.producer = producer;
        this.accountToJson = accountToJson;
        this.topic = topic;
    }

    @Override
    public void accept(List<Account> accounts)
    {
        accounts.forEach(a -> producer.send(toProducerRecord(a)));
    }

    protected ProducerRecord<String, String> toProducerRecord(Account account)
    {
        String json = accountToJson.apply(account);
        return new ProducerRecord<String,String>(topic,String.valueOf(account.getId()),json);
    }

    public void close()
    {
        producer.close();
    }
}
