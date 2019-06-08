package com.phonepe.platform.bonsai.core.structures;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author tushar.naik
 * @version 1.0  27/09/18 - 12:45 PM
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CollectorFunctions {

    public static <K, V> Collector<Pair<K, V>, ?, LinkedHashMap<K, V>> pairLinkedHashMapCollector() {
        return Collectors.toMap(Pair::getK, Pair::getV, (k1, k2) -> k1, LinkedHashMap::new);
    }

    public static <K, V> Collector<Pair<K, V>, ?, Map<K, V>> pairMapCollector() {
        return Collectors.toMap(Pair::getK, Pair::getV, (k1, k2) -> k1);
    }

    public static <K, V> Collector<Pair<K, V>, ?, Map<K, V>> pairMapCollector(Supplier<Map<K, V>> mapSupplier) {
        return Collectors.toMap(Pair::getK, Pair::getV, (k1, k2) -> k1, mapSupplier);
    }
}
