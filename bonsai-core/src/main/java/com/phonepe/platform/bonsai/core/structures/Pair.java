package com.phonepe.platform.bonsai.core.structures;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author tushar.naik
 * @version 1.0  27/09/18 - 12:42 PM
 */
@Data
@AllArgsConstructor
public class Pair<K, V> {
    private K k;
    private V v;

    public static <K, V> Pair<K, V> create(K k, V v) {
        return new Pair<>(k, v);
    }
}
