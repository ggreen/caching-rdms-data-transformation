package com.github.ggreen.caching.rdms.pipeline;

import com.github.ggreen.caching.rdms.domain.Account;
import com.github.ggreen.caching.rdms.domain.AccountRepository;
import com.github.ggreen.caching.rdms.domain.JsonToAccount;
import com.github.ggreen.caching.rdms.domain.jdbc.AccountGeodeRepository;
import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.settings.Settings;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;

import java.io.IOException;
import java.util.Properties;

public class AccountGeodeSink
{
    private final StreamsBuilder builder;
    private final KStream<String, String> accountStream;
    private final JsonToAccount jsonToAccount;
    private final AccountRepository accountRepository;
    private final Settings settings;
    private final Properties properties;

    public AccountGeodeSink()
    {
        this(new StreamsBuilder(),
                new JsonToAccount(),
                new AccountGeodeRepository(),
                Config.getSettings());


    }

    public AccountGeodeSink(StreamsBuilder builder, JsonToAccount jsonToAccount, AccountRepository accountRepository,
                            Settings settings)
    {
        this.jsonToAccount = jsonToAccount;
        this.accountRepository = accountRepository;
        this.settings = settings;


        this.properties = new Properties();
        this.configure(properties, settings);

        this.builder = builder;

        this.accountStream = builder.stream("accounts");
        this.accountStream.foreach((accountId, json) -> receive(accountId, json));



    }

    protected void receive(String key, String json)
    {
        System.out.println("key:"+key+" json:"+json);
        Account account = jsonToAccount.apply(json);
        this.accountRepository.save(account);
    }

    private void configure(Properties props, Settings settings)
    {
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, settings.getProperty("APPLICATION_ID_CONFIG","accounts-application"));
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, settings.getProperty("BOOTSTRAP_SERVERS_CONFIG","localhost:9092"));
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, settings.getPropertyClass("KEY_SERDE_CLASS_CONFIG",Serdes.String().getClass()));
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, settings.getPropertyClass("VALUE_SERDE_CLASS_CONFIG",Serdes.String().getClass()));

    }


    protected Properties getProperties()
    {
        return properties;
    }

    public static void main(String[] args) throws IOException
    {
        AccountGeodeSink sink = new AccountGeodeSink();
        KafkaStreams streams = new KafkaStreams(sink.builder.build(),
                sink.properties);

        streams.start();
        System.in.read();
    }
}
