package com.github.ggreen.caching.rdms.pipeline;

import com.github.ggreen.caching.rdms.domain.Account;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.apache.geode.cache.Region;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class GeodeConsumerTest
{
    @Test
    void when_accept_saves_to_geode()
    {
        Region<Long,Account> region = mock(Region.class);
        GeodeConsumer geodeConsumer = new GeodeConsumer(region);
        Account expected = new JavaBeanGeneratorCreator<>(Account.class)
                .randomizeAll().create();

        List<Account> list = Collections.singletonList(expected);
        geodeConsumer.accept(list);
        verify(region).putAll(any());
    }
}