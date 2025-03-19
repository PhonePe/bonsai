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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConditionTest {

    private Condition condition;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDefaultConstructor() {
        condition = new Condition();
        
        // Default values should be set
        assertTrue(condition.isLive());
        assertEquals(100.0f, condition.getPercentage());
        assertEquals(Collections.emptyMap(), condition.getProperties());
    }

    @Test
    void testParameterizedConstructor() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("key1", "value1");
        properties.put("key2", 123);
        
        condition = new Condition(false, 50.0f, properties);
        
        // Values should match constructor parameters
        assertFalse(condition.isLive());
        assertEquals(50.0f, condition.getPercentage());
        assertEquals(properties, condition.getProperties());
    }

    @Test
    void testGetPropertyWithExistingKey() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("stringKey", "stringValue");
        properties.put("intKey", 123);
        properties.put("booleanKey", true);
        
        condition = new Condition(true, 100.0f, properties);
        
        // Should return the correct value for existing keys
        assertEquals("stringValue", condition.getProperty("stringKey", "default"));
        assertEquals(123, condition.getProperty("intKey", 0));
        assertEquals(true, condition.getProperty("booleanKey", false));
    }

    @Test
    void testGetPropertyWithNonExistingKey() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("key1", "value1");
        
        condition = new Condition(true, 100.0f, properties);
        
        // Should return the default value for non-existing keys
        assertEquals("default", condition.getProperty("nonExistingKey", "default"));
        assertEquals(0, condition.getProperty("nonExistingKey", 0));
        assertFalse(condition.getProperty("nonExistingKey", false));
    }

    @Test
    void testGetPropertyWithNullProperties() {
        condition = new Condition(true, 100.0f, null);
        
        // Should return the default value when properties is null
        assertEquals("default", condition.getProperty("anyKey", "default"));
    }

    @Test
    void testGetPropertyWithEmptyProperties() {
        condition = new Condition(true, 100.0f, Collections.emptyMap());
        
        // Should return the default value when properties is empty
        assertEquals("default", condition.getProperty("anyKey", "default"));
    }

    @Test
    void testGetPropertyWithTypeCastException() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("key1", 123); // Integer value
        
        condition = new Condition(true, 100.0f, properties);
        
        // Should return the default value when type casting fails
        final var key1 = condition.getProperty("key1", false);
        assertEquals(false, key1);
    }

    @Test
    void testSetters() {
        condition = new Condition();
        
        // Test setters
        condition.setLive(false);
        condition.setPercentage(75.0f);
        
        Map<String, Object> newProperties = new HashMap<>();
        newProperties.put("newKey", "newValue");
        condition.setProperties(newProperties);
        
        // Values should be updated
        assertFalse(condition.isLive());
        assertEquals(75.0f, condition.getPercentage());
        assertEquals(newProperties, condition.getProperties());
    }

    @Test
    void testEqualsAndHashCode() {
        Map<String, Object> properties1 = new HashMap<>();
        properties1.put("key1", "value1");
        
        Map<String, Object> properties2 = new HashMap<>();
        properties2.put("key1", "value1");
        
        Condition condition1 = new Condition(true, 50.0f, properties1);
        Condition condition2 = new Condition(true, 50.0f, properties2);
        Condition condition3 = new Condition(false, 50.0f, properties1);
        
        // Equal conditions should have equal hashcodes
        assertEquals(condition1, condition2);
        assertEquals(condition1.hashCode(), condition2.hashCode());
        
        // Different conditions should not be equal
        assertNotEquals(condition1, condition3);
    }
}
