package com.github.ggreen.caching.rdms.pipeline;

import com.github.ggreen.caching.rdms.domain.AccountRepository;
import com.github.ggreen.caching.rdms.domain.JsonToAccount;
import nyla.solutions.core.util.settings.Settings;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
/* Properties props = new Properties();

      KafkaStreams streams = new KafkaStreams(builder.build(), props);
      streams.start();

      System.in.read();
      */
class AccountGeodeSinkTest
{
    private StreamsBuilder builder;
    private KStream kStream;
    private AccountGeodeSink subject;
    private JsonToAccount jsonToAccount;
    private AccountRepository repository;
    private Settings settings = mock(Settings.class);

    @BeforeEach
    void setUp()
    {
        builder = mock(StreamsBuilder.class);
        kStream = mock(KStream.class);
        jsonToAccount = mock(JsonToAccount.class);
        repository = mock(AccountRepository.class);
        when(builder.stream(anyString())).thenReturn(kStream);
        String property = "{}";
        when(settings.getProperty(anyString())).thenReturn(property);
        when(settings.getProperty(anyString(),anyString())).thenReturn(property);
        Class propertyClass = String.class;
        when(settings.getPropertyClass(anyString())).thenReturn(propertyClass);
        when(settings.getPropertyClass(anyString(),any(Class.class))).thenReturn(propertyClass);


        subject = new AccountGeodeSink(builder,jsonToAccount,repository,settings);
    }

    @Test
    void construct() throws IOException
    {
        verify(builder,atLeastOnce()).stream(anyString());
        verify(kStream,atLeastOnce()).foreach(any());
        verify(settings,atLeastOnce()).getProperty(anyString(),anyString());
        verify(settings,atLeastOnce()).getPropertyClass(any(),any(Class.class));

    }

    @Test
    void getProperties()
    {
        assertTrue(subject.getProperties().size() > 1);
    }

    @Test
    void receive()
    {
        String key = "key";
        String value = "value";

        subject.receive(key,value);
        verify(jsonToAccount).apply(anyString());
        verify(repository).save(any());
    }

}