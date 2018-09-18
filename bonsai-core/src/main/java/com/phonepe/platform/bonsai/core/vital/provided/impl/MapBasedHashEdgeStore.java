package com.phonepe.platform.bonsai.core.vital.provided.impl;

import com.google.common.collect.Maps;
import com.phonepe.platform.bonsai.core.structures.OrderedList;
import com.phonepe.platform.bonsai.core.vital.provided.EdgeStore;
import com.phonepe.platform.bonsai.core.vital.provided.model.AtomicEdge;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author tushar.naik
 * @version 1.0  22/08/18 - 5:34 PM
 */
@AllArgsConstructor
public class MapBasedHashEdgeStore implements EdgeStore<String, AtomicEdge> {
    private final Map<String, AtomicEdge> storage;

    public MapBasedHashEdgeStore() {
        storage = Maps.newHashMap();
    }

    @Override
    public AtomicEdge mapEdge(String s, AtomicEdge atomicEdge) {
        return storage.put(s, atomicEdge);
    }

    @Override
    public AtomicEdge get(String s) {
        return storage.get(s);
    }

    @Override
    public AtomicEdge delete(String s) {
        return storage.remove(s);
    }

    @Override
    public OrderedList<AtomicEdge> getAll(List<String> ids) {
        return ids.stream().map(storage::get).collect(Collectors.toCollection(OrderedList::new));
    }
}
