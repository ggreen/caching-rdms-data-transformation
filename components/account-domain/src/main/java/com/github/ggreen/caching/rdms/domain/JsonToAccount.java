package com.github.ggreen.caching.rdms.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nyla.solutions.core.exception.FormatException;

import java.util.function.Function;

public class JsonToAccount implements Function<String,Account>
{
    private final ObjectMapper objectMapper;

    public JsonToAccount()
    {
        this(new ObjectMapper());
    }

    public JsonToAccount(ObjectMapper objectMapper)
    {
        this.objectMapper = objectMapper;
    }

    @Override
    public Account apply(String json)
    {
        try {
            return objectMapper.readValue(json,Account.class);
        }
        catch (JsonProcessingException e) {
            throw new FormatException(e);
        }
    }
}
