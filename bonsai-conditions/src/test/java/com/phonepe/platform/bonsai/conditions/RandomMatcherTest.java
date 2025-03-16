package com.phonepe.platform.bonsai.conditions;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class RandomMatcherTest {

    @Test
    public void test100PercentMatcher() {
        RandomMatcher randomMatcher = new RandomMatcher();
        for (int i = 0; i < 10000; i++) {
            final Boolean match = randomMatcher.match(100);
            assertTrue(match);
        }
    }

    @Test
    public void testEqualRandom() {
        RandomMatcher randomMatcher = new RandomMatcher();
        int percentage = 25;
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
        assertEquals(100-percentage, (float) (falseValue) / (size) * 100, 0.1);
    }

    @Test
    public void test1000PercentMatcher() {
        RandomMatcher randomMatcher = new RandomMatcher(0, 1000);
        int percentage = 25;
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
        assertEquals(percentage, (float) (trueValue) / (size) * 1000, 1);
        assertEquals(1000 - percentage, (float) (falseValue) / (size) * 1000, 1);
    }
}