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
import com.phonepe.platform.bonsai.core.vital.provided.KeyTreeStore;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class InMemoryKeyTreeStore implements KeyTreeStore<String, String> {
    private final Map<String, String> storage;

    public InMemoryKeyTreeStore() {
        this.storage = Maps.newConcurrentMap();
    }

    @Override
    public boolean containsKey(String key) {
        if (key == null) {
            return false;
        }
        return storage.containsKey(key);
    }

    @Override
    public String createKeyTree(String key, String id) {
        if (key == null) {
            return null;
        }
        return storage.put(key, id);
    }

    @Override
    public String getKeyTree(String key) {
        if (key == null) {
            return null;
        }
        return storage.get(key);
    }

    @Override
    public String removeKeyTree(String key) {
        if (key == null) {
            return null;
        }
        return storage.remove(key);
    }
}
