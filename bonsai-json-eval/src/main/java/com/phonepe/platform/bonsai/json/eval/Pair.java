package com.phonepe.platform.bonsai.json.eval;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode
@Data
public class Pair<K, V> {
    private K key;
    private V value;
}
