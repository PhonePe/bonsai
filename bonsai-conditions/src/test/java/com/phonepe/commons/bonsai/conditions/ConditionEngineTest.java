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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConditionEngineTest {

    private TestConditionEngine conditionEngine;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        conditionEngine = new TestConditionEngine();
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testEqualityContention() {
        assertTrue(ConditionEngine.testEqualityContention("test", null));
        assertTrue(ConditionEngine.testEqualityContention("test", "test"));
        assertFalse(ConditionEngine.testEqualityContention("test", "other"));
    }

    @Test
    void testStringEqualityContention() {
        assertTrue(ConditionEngine.testStringEqualityContention("test", null));
        assertTrue(ConditionEngine.testStringEqualityContention("test", "test"));
        assertTrue(ConditionEngine.testStringEqualityContention("TEST", "test"));
        assertFalse(ConditionEngine.testStringEqualityContention("test", "other"));
    }

    @Test
    void testRegexContention() {
        assertTrue(ConditionEngine.testRegexContention("test123", null));
        assertTrue(ConditionEngine.testRegexContention("test123", "test\\d+"));
        assertFalse(ConditionEngine.testRegexContention("test", "\\d+"));
    }

    @Test
    void testContainsContention() {
        Set<String> iterable = new HashSet<>(Arrays.asList("a", "b", "c"));
        Set<String> value1 = new HashSet<>(Arrays.asList("a", "b"));
        Set<String> value2 = new HashSet<>(Arrays.asList("a", "b", "c", "d"));

        assertTrue(ConditionEngine.testContainsContention(iterable, null));
        assertFalse(ConditionEngine.testContainsContention(iterable, value2));
        assertFalse(ConditionEngine.testContainsContention(iterable,
                                                           value1)); // Should fail as intersection size is less than iterable size
    }

    @Test
    void testContainsAll() {
        assertTrue(ConditionEngine.testContainsAll("abcdef", null));
        assertTrue(ConditionEngine.testContainsAll("abcdef", Arrays.asList("abc", "def")));
        assertFalse(ConditionEngine.testContainsAll("abcdef", Arrays.asList("abc", "xyz")));
    }

    @Test
    void testContainsAny() {
        assertTrue(ConditionEngine.testContainsAny("abcdef", null));
        assertTrue(ConditionEngine.testContainsAny("abcdef", Arrays.asList("abc", "xyz")));
        assertFalse(ConditionEngine.testContainsAny("abcdef", Arrays.asList("xyz", "123")));
    }

    @Test
    void testExcludesAll() {
        assertTrue(ConditionEngine.testExcludesAll("abcdef", null));
        assertTrue(ConditionEngine.testExcludesAll("abcdef", Arrays.asList("xyz", "123")));
        assertFalse(ConditionEngine.testExcludesAll("abcdef", Arrays.asList("abc", "def")));
    }

    @Test
    void testExcludesAny() {
        assertTrue(ConditionEngine.testExcludesAny("abcdef", null));
        assertTrue(ConditionEngine.testExcludesAny("abcdef", Arrays.asList("xyz", "123")));
        assertFalse(ConditionEngine.testExcludesAny("abcdef", Arrays.asList("abc", "def")));
    }

    @Test
    void testContainsMap() {
        Map<String, String> contender = new HashMap<>();
        contender.put("key1", "value1");
        contender.put("key2", "value2");

        Map<String, String> value1 = new HashMap<>();
        value1.put("key1", "value1");

        Map<String, String> value2 = new HashMap<>();
        value2.put("key1", "value1");
        value2.put("key3", "value3");

        assertTrue(ConditionEngine.testContainsMap(contender, null));
        assertTrue(ConditionEngine.testContainsMap(contender, value1));
        assertFalse(ConditionEngine.testContainsMap(contender, value2));
    }

    @Test
    void testContainsContentionWithMap() {
        Map<String, String[]> contender = new HashMap<>();
        contender.put("key1", new String[]{"value1"});
        contender.put("key2", new String[]{"value2"});

        Map<String, String> value1 = new HashMap<>();
        value1.put("key1", "value1");

        Map<String, String> value2 = new HashMap<>();
        value2.put("key1", "value1");
        value2.put("key3", "value3");

        assertTrue(ConditionEngine.testContainsContention(contender, (Map<String, String>) null));
        assertTrue(ConditionEngine.testContainsContention(contender, value1));
        assertFalse(ConditionEngine.testContainsContention(contender, value2));
    }

    @Test
    void testContainsContentionWithList() {
        Map<String, String> contender = new HashMap<>();
        contender.put("key1", "value1");
        contender.put("key2", "value2");

        List<String> value1 = Arrays.asList("key1", "key2");
        List<String> value2 = Arrays.asList("key1", "key3");

        assertTrue(ConditionEngine.testContainsContention(contender, null));
        assertTrue(ConditionEngine.testContainsContention(contender, value1));
        assertFalse(ConditionEngine.testContainsContention(contender, value2));
    }

    @Test
    void testNotNull() {
        assertTrue(ConditionEngine.notNull("test"));
        assertFalse(ConditionEngine.notNull(null));
    }

    @Test
    void testCheckIfGreaterThan() {
        assertTrue(ConditionEngine.checkIfGreaterThan(10, null));
        assertTrue(ConditionEngine.checkIfGreaterThan(10, 5));
        assertFalse(ConditionEngine.checkIfGreaterThan(5, 10));
    }

    @Test
    void testCheckIfLesserThan() {
        assertTrue(ConditionEngine.checkIfLesserThan(5, null));
        assertTrue(ConditionEngine.checkIfLesserThan(5, 10));
        assertFalse(ConditionEngine.checkIfLesserThan(10, 5));
    }

    @Test
    void testCheckIfApplicableWithSupplier() {
        BooleanSupplier trueSupplier = () -> true;
        BooleanSupplier falseSupplier = () -> false;

        assertTrue(ConditionEngine.checkIfApplicable(null));
        assertTrue(ConditionEngine.checkIfApplicable(trueSupplier));
        assertFalse(ConditionEngine.checkIfApplicable(falseSupplier));
    }

    @Test
    void testCheckIfApplicableWithBiFunction() {
        BiPredicate<String, String> equalsFunction = String::equals;

        assertTrue(ConditionEngine.checkIfApplicable(null, "test", "other"));
        assertTrue(ConditionEngine.checkIfApplicable(equalsFunction, "test", null));
        assertTrue(ConditionEngine.checkIfApplicable(equalsFunction, "test", "test"));
        assertFalse(ConditionEngine.checkIfApplicable(equalsFunction, "test", "other"));
    }

    @Test
    void testBooleanEquality() {
        assertTrue(ConditionEngine.testBooleanEquality(true, null));
        assertTrue(ConditionEngine.testBooleanEquality(true, true));
        assertFalse(ConditionEngine.testBooleanEquality(true, false));
    }

    @Test
    void testMatch() {
        // Setup test data
        TestCondition condition1 = new TestCondition(true, 100f);
        TestCondition condition2 = new TestCondition(true, 75f);
        TestCondition condition3 = new TestCondition(false, 100f);
        List<TestCondition> conditions = Arrays.asList(condition1, condition2, condition3);

        // Create a test implementation with mock behavior
        TestConditionEngine engineSpy = spy(conditionEngine);

        // Configure the mocked behavior directly
        when(engineSpy.match("test", condition1)).thenReturn(true);
        when(engineSpy.match("test", condition2)).thenReturn(false);

        // Test the match method without modifying RANDOM_MATCHER
        Optional<TestCondition> result = engineSpy.match("test", conditions);

        // Verify correct condition was returned
        assertTrue(result.isPresent());
        assertEquals(condition1, result.get());

        // Verify condition3 was never checked (not live)
        verify(engineSpy, never()).match("test", condition3);
    }

    // Test implementation of ConditionEngine for testing
    private static class TestConditionEngine extends ConditionEngine<String, TestCondition> {
        @Override
        public Boolean match(String entity, TestCondition condition) {
            return true; // Default implementation, will be mocked in test
        }
    }

    // Test implementation of Condition for testing
    private static class TestCondition extends Condition {
        public TestCondition(boolean live, float percentage) {
            super(live, percentage, Collections.emptyMap());
        }
    }
}