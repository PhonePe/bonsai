/*
 *  Copyright (c) 2025 Original Author(s), PhonePe India Pvt. Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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
