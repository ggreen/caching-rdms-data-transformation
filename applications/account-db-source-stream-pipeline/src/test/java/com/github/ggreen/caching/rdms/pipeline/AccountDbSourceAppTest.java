package com.github.ggreen.caching.rdms.pipeline;

import nyla.solutions.core.exception.ConfigException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountDbSourceAppTest
{

    @Test
    void throwsMissingConfigPropertiesException()
    {
        String[] args = {};
        assertThrows(ConfigException.class, ()-> AccountDbSourceApp.main(args));
    }
}