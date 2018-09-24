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
    public boolean containsKey(String key) {
        return storage.containsKey(key);
    }

    @Override
    public boolean mapKnot(String id, Knot knot) {
        storage.put(id, knot);
        return true;
    }

    @Override
    public Knot get(String id) {
        return storage.get(id);
    }

    @Override
    public Knot delete(String s) {
        return storage.remove(s);
    }
}
