package com.phonepe.platform.bonsai.core.structures;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Use this in the streaming functions
 *
 * @author tushar.naik
 * @version 1.0  2019-04-16 - 22:50
 */
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MapEntry<K, V> {
    private K k;
    private V v;

    public static <K, V> MapEntry<K, V> of(K k, V v) {
        return new MapEntry<>(k, v);
    }

    /**
     * A collector to convert into a map (stream)
     *
     * @param <K> key
     * @param <V> value
     * @return collector
     */
    public static <K, V> Collector<MapEntry<K, V>, ?, Map<K, V>> mapCollector() {
        return Collectors.toMap(MapEntry::getK, MapEntry::getV);
    }

    public static <K, V> Collector<MapEntry<K, V>, ?, Map<K, V>> mapCollector(Supplier<Map<K, V>> mapSupplier) {
        return Collectors.toMap(MapEntry::getK, MapEntry::getV, (k1, k2) -> k1, mapSupplier);
    }
}