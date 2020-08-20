package com.github.ggreen.caching.rdms.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nyla.solutions.core.exception.FormatException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonToAccountTest
{


    @Test
    void apply()
    {
        JsonToAccount subject = new JsonToAccount();
        Account excepted = Account.builder().build();

        String json = new AccountToJson().apply(excepted);
        Account actual = subject.apply(json);
        assertEquals(excepted,actual);
    }

    @Test
    void apply_when_exception_throws_FormatException() throws JsonProcessingException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonToAccount subject = new JsonToAccount(objectMapper);

        assertThrows(FormatException.class,() ->  subject.apply("INVALID"));

    }
}