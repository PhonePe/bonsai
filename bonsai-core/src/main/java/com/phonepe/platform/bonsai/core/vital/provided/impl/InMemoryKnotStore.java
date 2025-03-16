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
import com.phonepe.platform.bonsai.core.vital.provided.KnotStore;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import lombok.AllArgsConstructor;

import java.util.Map;

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
