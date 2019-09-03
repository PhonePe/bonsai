package com.phonepe.platform.bonsai.core.vital.provided.impl;

import com.google.common.collect.Maps;
import com.phonepe.platform.bonsai.core.vital.provided.KeyTreeStore;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * @author tushar.naik
 * @version 1.0  22/08/18 - 5:34 PM
 */
@AllArgsConstructor
public class InMemoryKeyTreeStore implements KeyTreeStore<String, String> {
    private final Map<String, String> storage;

    public InMemoryKeyTreeStore() {
        this.storage = Maps.newConcurrentMap();
    }

    @Override
    public boolean containsKey(String key) {
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
