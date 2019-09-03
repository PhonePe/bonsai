package com.phonepe.platform.bonsai.core.vital.provided.impl;

import com.google.common.collect.Maps;
import com.phonepe.platform.bonsai.core.vital.provided.KnotStore;
import com.phonepe.platform.bonsai.models.blocks.Knot;
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
        this.storage = Maps.newConcurrentMap();
    }

    @Override
    public boolean containsKnot(String key) {
        if (key == null) {
            return false;
        }
        return storage.containsKey(key);
    }

    @Override
    public Knot mapKnot(String id, Knot knot) {
        if (id == null) {
            return null;
        }
        return storage.put(id, knot);
    }

    @Override
    public Knot getKnot(String id) {
        if (id == null) {
            return null;
        }
        return storage.get(id);
    }

    @Override
    public Knot deleteKnot(String id) {
        if (id == null) {
            return null;
        }
        return storage.remove(id);
    }
}
