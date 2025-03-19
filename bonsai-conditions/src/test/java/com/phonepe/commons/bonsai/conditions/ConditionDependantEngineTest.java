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
import org.mockito.Spy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConditionDependantEngineTest {

    @Spy
    private TestConditionDependantEngine conditionDependantEngine;

    private TestCondition condition1;
    private TestCondition condition2;
    private TestCondition condition3;
    private List<TestCondition> conditions;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create test conditions
        condition1 = new TestCondition(true, 100f);
        condition2 = new TestCondition(true, 75f);
        condition3 = new TestCondition(false, 100f);
        
        conditions = Arrays.asList(condition1, condition2, condition3);
    }

    @Test
    void testMatchWithCondition() {
        // Configure the spy to return specific values for match(condition)
        doReturn(true).when(conditionDependantEngine).match(condition1);
        doReturn(false).when(conditionDependantEngine).match(condition2);
        
        // Test match(Void, condition) delegates to match(condition)
        assertTrue(conditionDependantEngine.match(null, condition1));
        assertFalse(conditionDependantEngine.match(null, condition2));
        
        // Verify match(condition) was called
        verify(conditionDependantEngine, times(1)).match(condition1);
        verify(conditionDependantEngine, times(1)).match(condition2);
    }

    @Test
    void testMatchWithConditionList() {
        // Configure the spy to return specific values for match(condition)
        doReturn(true).when(conditionDependantEngine).match(condition1);
        doReturn(false).when(conditionDependantEngine).match(condition2);
        
        // Test match with list of conditions
        Optional<TestCondition> result = conditionDependantEngine.match(null, conditions);
        
        // Should return the first matching condition
        assertTrue(result.isPresent());
        assertEquals(condition1, result.get());
        
        // Verify match was called for live conditions only
        verify(conditionDependantEngine, times(1)).match(condition1);
        verify(conditionDependantEngine, never()).match(condition3); // condition3 is not live
    }

    @Test
    void testMatchWithNoMatchingConditions() {
        // Configure the spy to return false for all conditions
        doReturn(false).when(conditionDependantEngine).match(any(TestCondition.class));
        
        // Test match with list of conditions
        Optional<TestCondition> result = conditionDependantEngine.match(null, conditions);
        
        // Should return empty Optional when no conditions match
        assertFalse(result.isPresent());
    }

    @Test
    void testMatchWithEmptyConditionList() {
        // Test match with empty list
        Optional<TestCondition> result = conditionDependantEngine.match(null, Collections.emptyList());
        
        // Should return empty Optional for empty list
        assertFalse(result.isPresent());
    }

    // Test implementation of ConditionDependantEngine
    private static class TestConditionDependantEngine extends ConditionDependantEngine<TestCondition> {
        @Override
        public Boolean match(TestCondition c) {
            // This will be mocked in tests
            return true;
        }
    }

    // Test implementation of Condition
    private static class TestCondition extends Condition {
        public TestCondition(boolean live, float percentage) {
            super(live, percentage, Collections.emptyMap());
        }
    }
}
