package com.phonepe.platform.bonsai.core.vital.provided;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Stores<S, I, K, E> {
    private KeyTreeStore<S, I> keyTreeStore;
    private KnotStore<I, K> knotStore;
    private EdgeStore<I, E> edgeStore;
}
