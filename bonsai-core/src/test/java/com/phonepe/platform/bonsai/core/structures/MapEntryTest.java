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

package com.phonepe.platform.bonsai.core.structures;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.IntStream;

public class MapEntryTest {
    @Test
    void testMapEntry() {
        Map<String, Integer> collect = IntStream.range(1, 4)
                .mapToObj(k -> MapEntry.of(("a" + k), k))
                .collect(MapEntry.mapCollector());
        Assertions.assertEquals(ImmutableMap.of("a1", 1, "a2", 2, "a3", 3), collect);
    }
}