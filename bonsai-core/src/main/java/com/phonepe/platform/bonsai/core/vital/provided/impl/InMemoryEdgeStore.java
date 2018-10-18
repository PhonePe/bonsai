package com.phonepe.platform.bonsai.core.vital.provided.impl;

import com.google.common.collect.Maps;
import com.phonepe.platform.bonsai.core.vital.blocks.Edge;
import com.phonepe.platform.bonsai.core.vital.provided.EdgeStore;
import lombok.AllArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    public Edge getEdge(String s) {
        return storage.get(s);
    }

    @Override
    public Edge deleteEdge(String s) {
        return storage.remove(s);
    }

    @Override
    public LinkedHashMap<String, Edge> getAllEdges(List<String> ids) {
        LinkedHashMap<String, Edge> resultMapping = new LinkedHashMap<>();
        for (String id : ids) {
            resultMapping.put(id, storage.get(id));
        }
        return resultMapping;
    }
}
