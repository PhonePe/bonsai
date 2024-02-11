package com.phonepe.platform.bonsai.core.structures;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.IntStream;

public class MapEntryTest {
    @Test
    void testMapEntry() {
        Map<String, Integer> collect = IntStream.range(1, 4)
                .mapToObj(k -> MapEntry.of(("a" + k), k))
                .collect(MapEntry.mapCollector());
        Assertions.assertEquals(ImmutableMap.of("a1", 1, "a2", 2, "a3", 3), collect);
    }
}