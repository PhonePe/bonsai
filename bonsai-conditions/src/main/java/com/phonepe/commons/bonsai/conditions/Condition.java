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

package com.phonepe.commons.bonsai.conditions;

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
                Object value = properties.get(key);
                if (defaultValue != null && defaultValue.getClass().isInstance(value)) {
                    return (T) value;
                } else {
                    return defaultValue;
                }
            } catch (Exception e) {
                log.error("Exception while trying to cast:{}", properties.get(key), e);
                return defaultValue;
            }
        }
        return defaultValue;
    }

}
