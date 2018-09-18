package com.phonepe.platform.bonsai.core.vital.provided.impl;

import com.google.common.collect.Maps;
import com.phonepe.platform.bonsai.core.vital.provided.KnotStore;
import com.phonepe.platform.bonsai.core.vital.provided.model.AtomicKnot;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * @author tushar.naik
 * @version 1.0  22/08/18 - 5:34 PM
 */
@AllArgsConstructor
public class MapBasedKnotStore implements KnotStore<String, AtomicKnot> {
    private final Map<String, AtomicKnot> storage;

    public MapBasedKnotStore() {
        this.storage = Maps.newHashMap();
    }

    @Override
    public boolean containsKey(String key) {
        return storage.containsKey(key);
    }

    @Override
    public boolean mapKnot(String id, AtomicKnot knot) {
        storage.put(id, knot);
        return true;
    }

    @Override
    public AtomicKnot get(String id) {
        return storage.get(id);
    }

    @Override
    public AtomicKnot delete(String s) {
        return storage.remove(s);
    }
}
