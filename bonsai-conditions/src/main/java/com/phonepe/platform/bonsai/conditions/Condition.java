package com.phonepe.platform.bonsai.conditions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Condition {

    /* matching criteria */
    private boolean live = true;    // live by default
    private float percentage = 100.0f;

    /* matched properties */
    Map<String, Object> properties = Collections.emptyMap();

    /**
     * get a property, for a given key, if it isnt present, return the default value
     *
     * @param key          string key
     * @param defaultValue default typed calue
     * @param <T>          type of value
     * @return value for key if present, else default value
     */
    @SuppressWarnings("unchecked")
    public <T> T getProperty(String key, T defaultValue) {
        if (properties != null && !properties.isEmpty() && properties.containsKey(key)) {
            try {
                return (T) properties.get(key);
            } catch (Exception e) {
                log.error("Exception while trying to cast:{}", properties.get(key), e);
                return defaultValue;
            }
        }
        return defaultValue;
    }

}
