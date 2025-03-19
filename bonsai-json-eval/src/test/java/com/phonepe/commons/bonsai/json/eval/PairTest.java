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

package com.phonepe.commons.bonsai.json.eval;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PairTest {

    @Test
    void testPairCreation() {
        Pair<String, Integer> pair = new Pair<>("key", 42);
        Assertions.assertEquals("key", pair.getKey());
        Assertions.assertEquals(42, pair.getValue());
    }

    @Test
    void testPairSetters() {
        Pair<String, Integer> pair = new Pair<>("key", 42);
        pair.setKey("newKey");
        pair.setValue(100);
        Assertions.assertEquals("newKey", pair.getKey());
        Assertions.assertEquals(100, pair.getValue());
    }

    @Test
    void testPairEquality() {
        Pair<String, Integer> pair1 = new Pair<>("key", 42);
        Pair<String, Integer> pair2 = new Pair<>("key", 42);
        Pair<String, Integer> pair3 = new Pair<>("key", 43);
        Pair<String, Integer> pair4 = new Pair<>("otherKey", 42);

        Assertions.assertEquals(pair1, pair2);
        Assertions.assertNotEquals(pair1, pair3);
        Assertions.assertNotEquals(pair1, pair4);
    }

    @Test
    void testPairHashCode() {
        Pair<String, Integer> pair1 = new Pair<>("key", 42);
        Pair<String, Integer> pair2 = new Pair<>("key", 42);

        Assertions.assertEquals(pair1.hashCode(), pair2.hashCode());
    }

    @Test
    void testPairToString() {
        Pair<String, Integer> pair = new Pair<>("key", 42);
        String toString = pair.toString();
        
        Assertions.assertTrue(toString.contains("key"));
        Assertions.assertTrue(toString.contains("42"));
    }
}
