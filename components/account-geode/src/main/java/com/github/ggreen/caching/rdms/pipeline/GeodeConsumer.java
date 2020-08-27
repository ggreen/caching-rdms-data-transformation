package com.github.ggreen.caching.rdms.pipeline;

import com.github.ggreen.caching.rdms.domain.Account;
import io.pivotal.services.dataTx.geode.client.GeodeClient;
import org.apache.geode.cache.Region;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Gregory Green
 */
public class GeodeConsumer implements Consumer<List<Account>>
{
    private final Region<Long, Account> region;

    public GeodeConsumer()
    {
        this(GeodeClient.connect().getRegion("accounts"));
    }
    public GeodeConsumer(Region<Long, Account> region)
    {
        this.region = region;
    }

    @Override
    public void accept(List<Account> list)
    {
        Map<Long, Account> map = list.stream()
                                     .collect(
                                   Collectors
                                    .toMap(Account::getId,
                                            account -> account
                                     , (a1,a2) -> { return a1;}));

        region.putAll(map);

    }
}
