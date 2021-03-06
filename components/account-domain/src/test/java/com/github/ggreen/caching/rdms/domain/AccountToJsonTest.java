package com.github.ggreen.caching.rdms.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nyla.solutions.core.exception.FormatException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccountToJsonTest
{
    @Test
    void apply()
    {
        AccountToJson subject = new AccountToJson();
        final long currentTimestamp = System.currentTimeMillis();
        Account account = new Account(1L,"hello", currentTimestamp);
        String expected =  "{\"id\":1,\"name\":\"hello\",\"currentTimestamp\":"+currentTimestamp+"}";

        assertEquals(expected,subject.apply(account));

    }

    @Test
    void when_JsonProcessingException_throws_FormatException() throws JsonProcessingException
    {
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        when(objectMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);
        AccountToJson subject = new AccountToJson(objectMapper);
        Account account = new Account();

        assertThrows(FormatException.class,() ->subject.apply(account));

    }
}