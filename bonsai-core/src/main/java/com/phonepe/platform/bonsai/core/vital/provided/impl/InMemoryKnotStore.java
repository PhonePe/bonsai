package com.phonepe.platform.bonsai.core.vital.provided.impl;

import com.google.common.collect.Maps;
import com.phonepe.platform.bonsai.core.vital.provided.KnotStore;
import com.phonepe.platform.bonsai.core.vital.blocks.Knot;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * @author tushar.naik
 * @version 1.0  22/08/18 - 5:34 PM
 */
@AllArgsConstructor
public class InMemoryKnotStore implements KnotStore<String, Knot> {
    private final Map<String, Knot> storage;

    public InMemoryKnotStore() {
        this.storage = Maps.newHashMap();
    }

    @Override
    public boolean containsKnot(String key) {
        return storage.containsKey(key);
    }

    @Override
    public Knot mapKnot(String id, Knot knot) {
        return storage.put(id, knot);
    }

    @Override
    public Knot getKnot(String id) {
        return storage.get(id);
    }

    @Override
    public Knot deleteKnot(String id) {
        return storage.remove(id);
    }
}
