package com.github.ggreen.caching.rdms.pipeline;

import com.github.ggreen.caching.rdms.domain.Account;
import com.github.ggreen.caching.rdms.domain.AccountToJson;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountKafkaSenderTest
{
    private Producer<String,String> producer;
    private AccountKafkaSender subject;
    private AccountToJson accountToJson;
    private Account account;
    private String topic  = "topic";

    @BeforeEach
    void setUp()
    {
        producer = mock(Producer.class);
        accountToJson = mock(AccountToJson.class);
        subject = new AccountKafkaSender(producer,accountToJson,topic);
       account = new JavaBeanGeneratorCreator<>(Account.class)
                .randomizeAll().create();
    }

    @Test
    void accept()
    {
        List<Account> accounts = Arrays.asList(account);
        subject.accept(accounts);

        verify(accountToJson,times(accounts.size())).apply(account);
        verify(producer,times(accounts.size())).send(any());
    }

    @Test
    void toProducerRecord()
    {
        String expectJson = "{}";
        when(accountToJson.apply(account)).thenReturn(expectJson);
       ProducerRecord<String,String> actual = subject.toProducerRecord(account);
       assertNotNull(actual);
       assertEquals(topic,actual.topic());


        assertEquals(expectJson, actual.value());
        assertNotNull(String.valueOf(account.getId()),actual.key());

       verify(accountToJson).apply(account);
    }

    @Test
    void close()
    {
        subject.close();
        verify(producer).close();

    }
}