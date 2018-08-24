package com.phonepe.platform.bonsai.core.vital.provided.impl;

import com.google.common.collect.Maps;
import com.phonepe.platform.bonsai.core.vital.provided.MappingStore;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * @author tushar.naik
 * @version 1.0  22/08/18 - 5:34 PM
 */
@AllArgsConstructor
public class MapBasedMappingStore implements MappingStore<String, String> {
    private final Map<String, String> storage;

    public MapBasedMappingStore() {
        this.storage = Maps.newHashMap();
    }

    @Override
    public boolean containsKey(String key) {
        return storage.containsKey(key);
    }

    @Override
    public boolean map(String key, String id) {
        return storage.put(key, id) != null;
    }

    @Override
    public String get(String id) {
        return storage.get(id);
    }
}
