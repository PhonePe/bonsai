package com.phonepe.platform.bonsai.core.vital.provided.impl;

import com.google.common.collect.Maps;
import com.phonepe.platform.bonsai.core.vital.provided.EdgeStore;
import com.phonepe.platform.bonsai.core.vital.blocks.Edge;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author tushar.naik
 * @version 1.0  22/08/18 - 5:34 PM
 */
@AllArgsConstructor
public class InMemoryEdgeStore implements EdgeStore<String, Edge> {
    private final Map<String, Edge> storage;

    public InMemoryEdgeStore() {
        storage = Maps.newHashMap();
    }

    @Override
    public Edge mapEdge(String s, Edge edge) {
        return storage.put(s, edge);
    }

    @Override
    public Edge get(String s) {
        return storage.get(s);
    }

    @Override
    public Edge delete(String s) {
        return storage.remove(s);
    }

    @Override
    public List<Edge> getAll(List<String> ids) {
        return ids.stream().map(storage::get).collect(Collectors.toList());
    }
}
