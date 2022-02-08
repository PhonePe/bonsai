package com.phonepe.platform.bonsai.core.vital.provided.impl;

import com.google.common.collect.Maps;
import com.phonepe.platform.bonsai.core.vital.provided.EdgeStore;
import com.phonepe.platform.bonsai.models.blocks.Edge;
import lombok.AllArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class InMemoryEdgeStore implements EdgeStore<String, Edge> {
    private final Map<String, Edge> storage;

    public InMemoryEdgeStore() {
        storage = Maps.newConcurrentMap();
    }

    @Override
    public boolean containsEdge(String id) {
        if (id == null) {
            return false;
        }
        return storage.containsKey(id);
    }

    @Override
    public Edge mapEdge(String id, Edge edge) {
        if (id == null) {
            return null;
        }
        return storage.put(id, edge);
    }

    @Override
    public Edge getEdge(String id) {
        if (id == null) {
            return null;
        }
        return storage.get(id);
    }

    @Override
    public Edge deleteEdge(String id) {
        if (id == null) {
            return null;
        }
        return storage.remove(id);
    }

    @Override
    public Map<String, Edge> getAllEdges(List<String> ids) {
        LinkedHashMap<String, Edge> resultMapping = new LinkedHashMap<>();
        for (String id : ids) {
            resultMapping.put(id, storage.get(id));
        }
        return resultMapping;
    }
}
