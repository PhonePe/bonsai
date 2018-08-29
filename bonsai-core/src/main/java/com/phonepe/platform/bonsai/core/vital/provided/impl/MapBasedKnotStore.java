package com.phonepe.platform.bonsai.core.vital.provided.impl;

import com.google.common.collect.Maps;
import com.phonepe.platform.bonsai.core.vital.provided.KnotStore;
import com.phonepe.platform.bonsai.core.vital.provided.model.AtomicEdge;
import com.phonepe.platform.bonsai.core.vital.provided.model.AtomicKnot;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * @author tushar.naik
 * @version 1.0  22/08/18 - 5:34 PM
 */
@AllArgsConstructor
public class MapBasedKnotStore implements KnotStore<String, AtomicKnot, AtomicEdge> {
    private final Map<String, AtomicKnot> storage;

    public MapBasedKnotStore() {
        this.storage = Maps.newHashMap();
    }

    @Override
    public boolean containsKey(String key) {
        return storage.containsKey(key);
    }

    @Override
    public boolean create(String s, AtomicKnot knot) {
        return storage.put(s, knot) != null;
    }

    @Override
    public boolean update(AtomicKnot knot) {
        return storage.put(knot.getId(), knot) != null;
    }

    @Override
    public AtomicKnot get(String s) {
        return storage.get(s);
    }
}
