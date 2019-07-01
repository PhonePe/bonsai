package com.phonepe.platform.bonsai.core.vital.provided;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author tushar.naik
 * @version 1.0  2019-06-29 - 18:18
 */
@AllArgsConstructor
@Getter
public class Stores<S, I, K, E> {
    private KeyTreeStore<S, I> keyTreeStore;
    private KnotStore<I, K> knotStore;
    private EdgeStore<I, E> edgeStore;
}
