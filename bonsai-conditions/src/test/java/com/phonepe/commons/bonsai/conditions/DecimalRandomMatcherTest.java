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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class DecimalRandomMatcherTest {

    private DecimalRandomMatcher matcher;

    @BeforeEach
    void setUp() {
        matcher = new DecimalRandomMatcher();
    }

    @Test
    void testDefaultConstructor() {
        matcher = new DecimalRandomMatcher();
        // Default constructor should use default bounds (0-100)
        assertNotNull(matcher);
    }

    @Test
    void testCustomBoundsConstructor() {
        matcher = new DecimalRandomMatcher(10, 1000);
        // Custom constructor should set the bounds
        assertNotNull(matcher);
    }

    @Test
    void testDefaultBounds() {
        assertFalse(matcher.match(0));
        assertTrue(matcher.match(100));
    }

    @RepeatedTest(100)
    void testMatchDistribution() {
        // Using real implementation to test distribution
        matcher = new DecimalRandomMatcher();

        // Running 10000 trials with 50% threshold should give us approx 50% matches
        int trials = 10000;
        float threshold = 50f;

        long matchCount = IntStream.range(0, trials)
                .mapToObj(i -> matcher.match(threshold))
                .filter(Boolean::booleanValue)
                .count();

        // The percentage should be close to the threshold with some margin for randomness
        float matchPercentage = (float) matchCount / trials * 100;
        assertEquals(threshold, matchPercentage, 5.0,
                     "Match percentage should be within 5% of the threshold");
    }

    @Test
    void test100PercentMatcher() {
        RandomMatcher randomMatcher = new DecimalRandomMatcher();
        for (int i = 0; i < 10000; i++) {
            final Boolean match = randomMatcher.match(100);
            assertTrue(match);
        }
    }

    @Test
    void testEqualRandom() {
        RandomMatcher randomMatcher = new DecimalRandomMatcher();
        float percentage = 2.0f;
        int size = 1000000;
        int trueValue = 0;
        int falseValue = 0;
        for (int i = 0; i < size; i++) {
            final Boolean match = randomMatcher.match(percentage);
            if (match) {
                trueValue++;
            } else {
                falseValue++;
            }
        }
        assertEquals(percentage, (float) (trueValue) / (size) * 100, 0.1);
        assertEquals(100 - percentage, (float) (falseValue) / (size) * 100, 0.1);
    }

    @Test
    void test1000PercentMatcher() {
        RandomMatcher randomMatcher = new DecimalRandomMatcher(0, 1000);
        float percentage = 2.35f;
        int size = 10000000;
        int trueValue = 0;
        int falseValue = 0;
        for (int i = 0; i < size; i++) {
            final Boolean match = randomMatcher.match(percentage);
            if (match) {
                trueValue++;
            } else {
                falseValue++;
            }
        }
        assertEquals(percentage, (float) (trueValue) / (size) * 1000, 0.1);
        assertEquals(1000 - percentage, (float) (falseValue) / (size) * 1000, 0.1);
    }

    @Test
    void testCustomBounds() {
        matcher = new DecimalRandomMatcher(10, 1000);

        assertFalse(matcher.match(10));
        assertFalse(matcher.match(9.99));
        assertTrue(matcher.match(1000));
        assertTrue(matcher.match(1000.01));
    }
}
