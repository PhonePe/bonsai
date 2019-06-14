package com.phonepe.platform.bonsai.core.structures;

import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.stream.IntStream;

/**
 * @author tushar.naik
 * @version 1.0  2019-06-14 - 15:28
 */
public class MapEntryTest {
    @Test
    public void testMapEntry() {
        Map<String, Integer> collect = IntStream.range(1, 4)
                                                .mapToObj(k -> MapEntry.of(("a" + k), k))
                                                .collect(MapEntry.mapCollector());
        Assert.assertEquals(ImmutableMap.of("a1", 1, "a2", 2, "a3", 3), collect);
    }
}