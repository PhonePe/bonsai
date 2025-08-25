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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MatcherTest {

    private Matcher.UniMatcher<String, Integer> uniMatcher;
    private Matcher.BooleanUniMatcher<String> booleanUniMatcher;
    private Matcher.ConditionalMatcher<String, TestCondition, String> conditionalMatcher;

    private TestCondition condition1;
    private List<TestCondition> conditions;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create test conditions
        condition1 = new TestCondition(true, 100f);
        TestCondition condition2 = new TestCondition(true, 75f);

        conditions = Arrays.asList(condition1, condition2);

        // Create test matchers using lambda implementations
        uniMatcher = value -> "Value: " + value;

        booleanUniMatcher = value -> value.contains("test");

        conditionalMatcher = new Matcher.ConditionalMatcher<>() {
            @Override
            public Optional<TestCondition> match(String value, List<TestCondition> conditionList) {
                return conditionList.stream()
                        .filter(condition -> match(value, condition))
                        .findFirst();
            }

            @Override
            public Boolean match(String value, TestCondition condition) {
                return value.length() > 5 && condition.isLive();
            }

            @Override
            public Optional<TestCondition> match(String value, List<TestCondition> conditionList, String associatedEntity) {
                return conditionList.stream()
                        .filter(condition -> match(value, condition, associatedEntity))
                        .findFirst();
            }

            @Override
            public Boolean match(String value, TestCondition condition, String associatedEntity) {
                boolean keyIsValid = "valid_key".equals(associatedEntity);
                return value.length() > 5 && condition.isLive() && keyIsValid;
            }
        };
    }

    @Test
    void testUniMatcher() {
        // Test UniMatcher implementation
        assertEquals("Value: 42", uniMatcher.match(42));
        assertEquals("Value: 100", uniMatcher.match(100));
    }

    @Test
    void testBooleanUniMatcher() {
        // Test BooleanUniMatcher implementation
        assertTrue(booleanUniMatcher.match("This is a test string"));
        assertFalse(booleanUniMatcher.match("No match here"));
    }

    @Test
    void testConditionalMatcherWithMatchingCondition() {
        // Test ConditionalMatcher with a value that matches
        Optional<TestCondition> result = conditionalMatcher.match("test string", conditions);

        // Should return the first matching condition
        assertTrue(result.isPresent());
        assertEquals(condition1, result.get());
    }

    @Test
    void testConditionalMatcherWithNonMatchingValue() {
        // Test ConditionalMatcher with a value that doesn't match (too short)
        Optional<TestCondition> result = conditionalMatcher.match("short", conditions);

        // Should return empty Optional since value doesn't match any condition
        assertFalse(result.isPresent());
    }

    @Test
    void testConditionalMatcherWithNonLiveCondition() {
        // Create a non-live condition
        TestCondition nonLiveCondition = new TestCondition(false, 100f);

        // Test match with non-live condition
        Boolean result = conditionalMatcher.match("test string", nonLiveCondition);

        // Should return false since condition is not live
        assertFalse(result);
    }

    @Test
    void testConditionalMatcherWithEmptyList() {
        // Test match with empty list
        Optional<TestCondition> result = conditionalMatcher.match("test string", Collections.emptyList());

        // Should return empty Optional for empty list
        assertFalse(result.isPresent());
    }

    @Test
    void testConditionalMatcherDirectMatch() {
        // Test direct match method
        assertTrue(conditionalMatcher.match("test string", condition1));
        assertFalse(conditionalMatcher.match("short", condition1));
    }

    @Test
    void testConditionalMatcherWithAssociatedEntity_Success() {
        Optional<TestCondition> result = conditionalMatcher.match("test string", conditions, "valid_key");

        assertTrue(result.isPresent());
        assertEquals(condition1, result.get());
    }

    @Test
    void testConditionalMatcherWithAssociatedEntity_Failure() {
        // Test with a valid value but an invalid key
        Optional<TestCondition> result = conditionalMatcher.match("test string", conditions, "invalid_key");

        // Should fail because the associatedEntity doesn't match
        assertFalse(result.isPresent());
    }

    @Test
    void testConditionalMatcherDirectMatchWithAssociatedEntity() {
        // Test the direct boolean-returning match method with the associatedEntity
        assertTrue(conditionalMatcher.match("a long enough string", condition1, "valid_key"));
        assertFalse(conditionalMatcher.match("a long enough string", condition1, "invalid_key"));
        assertFalse(conditionalMatcher.match("short", condition1, "valid_key"));
    }

    // Test implementation of Condition
    private static class TestCondition extends Condition {
        public TestCondition(boolean live, float percentage) {
            super(live, percentage, Collections.emptyMap());
        }
    }
}
