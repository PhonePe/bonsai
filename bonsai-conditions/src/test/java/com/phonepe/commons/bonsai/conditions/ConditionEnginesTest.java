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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ConditionEnginesTest {

    private ConditionEngine<Void, TestCondition> trueConditionEngine;
    private TestCondition condition1;
    private TestCondition condition2;
    private List<TestCondition> conditions;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create a true condition engine
        trueConditionEngine = ConditionEngines.trueConditionEngine();
        
        // Create test conditions
        condition1 = new TestCondition(true, 100f);
        condition2 = new TestCondition(true, 75f);
        
        conditions = Arrays.asList(condition1, condition2);
    }

    @Test
    void testTrueConditionEngineMatch() {
        // Test that TrueConditionEngine always returns true for match
        assertTrue(trueConditionEngine.match(null, condition1));
        assertTrue(trueConditionEngine.match(null, condition2));
    }

    @Test
    void testTrueConditionEngineMatchWithList() {
        // Test match with list of conditions
        Optional<TestCondition> result = trueConditionEngine.match(null, conditions);
        
        // Should return the first condition since TrueConditionEngine always matches
        assertTrue(result.isPresent());
        assertEquals(condition1, result.get());
    }

    @Test
    void testTrueConditionEngineMatchWithEmptyList() {
        // Test match with empty list
        Optional<TestCondition> result = trueConditionEngine.match(null, Collections.emptyList());
        
        // Should return empty Optional for empty list
        assertFalse(result.isPresent());
    }

    @Test
    void testTrueConditionEngineWithNonLiveCondition() {
        // Create a non-live condition
        TestCondition nonLiveCondition = new TestCondition(false, 100f);
        
        // Test match with non-live condition
        Optional<TestCondition> result = trueConditionEngine.match(null, Collections.singletonList(nonLiveCondition));
        
        // Should return empty Optional since condition is not live
        assertFalse(result.isPresent());
    }

    @Test
    void testTrueConditionEngineWithMixedConditions() {
        // Create a mix of live and non-live conditions
        TestCondition liveCondition = new TestCondition(true, 100f);
        TestCondition nonLiveCondition = new TestCondition(false, 100f);
        
        List<TestCondition> mixedConditions = Arrays.asList(nonLiveCondition, liveCondition);
        
        // Test match with mixed conditions
        Optional<TestCondition> result = trueConditionEngine.match(null, mixedConditions);
        
        // Should return the first live condition
        assertTrue(result.isPresent());
        assertEquals(liveCondition, result.get());
    }

    // Test implementation of Condition
    private static class TestCondition extends Condition {
        public TestCondition(boolean live, float percentage) {
            super(live, percentage, Collections.emptyMap());
        }
    }
}
