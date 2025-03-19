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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UtilsTest {
    @Test
    void testLeftPadWithChar() {
        Assertions.assertEquals("iiiimoveit", Utils.leftPad("moveit", 10, 'i'));
        Assertions.assertEquals("moveit", Utils.leftPad("moveit", 6, 'i'));
        Assertions.assertEquals("moveit", Utils.leftPad("moveit", 5, 'i'));
        Assertions.assertEquals("moveit", Utils.leftPad("moveit", 0, 'i'));
        Assertions.assertEquals("moveit", Utils.leftPad("moveit", -1, 'i'));
        Assertions.assertNull(Utils.leftPad(null, 10, 'i'));
    }

    @Test
    void testLeftPadWithString() {
        Assertions.assertEquals("iliketoiliketomoveit", Utils.leftPad("moveit", 20, "iliketo"));
        Assertions.assertEquals("abcabcamoveit", Utils.leftPad("moveit", 13, "abc"));
        Assertions.assertEquals("moveit", Utils.leftPad("moveit", 6, "abc"));
        Assertions.assertEquals("moveit", Utils.leftPad("moveit", 5, "abc"));
        Assertions.assertEquals("moveit", Utils.leftPad("moveit", 0, "abc"));
        Assertions.assertEquals("moveit", Utils.leftPad("moveit", -1, "abc"));
        Assertions.assertEquals(" moveit", Utils.leftPad("moveit", 7, ""));
        Assertions.assertEquals(" moveit", Utils.leftPad("moveit", 7, null));
        Assertions.assertNull(Utils.leftPad(null, 10, "abc"));
    }

    @Test
    void testLeftPadWithLargePadSize() {
        // Test with a pad size > 8192 which should trigger the recursive call
        String result = Utils.leftPad("test", 10000, 'x');
        Assertions.assertEquals(10000, result.length());
        Assertions.assertTrue(result.endsWith("test"));
        Assertions.assertEquals('x', result.charAt(0));
        Assertions.assertEquals('x', result.charAt(9995));
    }

    @Test
    void testIsEmptyString() {
        Assertions.assertTrue(Utils.isEmpty((String) null));
        Assertions.assertTrue(Utils.isEmpty(""));
        Assertions.assertFalse(Utils.isEmpty(" "));
        Assertions.assertFalse(Utils.isEmpty("test"));
    }

    @Test
    void testIsEmptyCollection() {
        Assertions.assertTrue(Utils.isEmpty((List<String>) null));
        Assertions.assertTrue(Utils.isEmpty(Collections.emptyList()));
        Assertions.assertFalse(Utils.isEmpty(Collections.singletonList("test")));
        Assertions.assertFalse(Utils.isEmpty(Arrays.asList("test1", "test2")));
        
        List<String> emptyArrayList = new ArrayList<>();
        Assertions.assertTrue(Utils.isEmpty(emptyArrayList));
        
        emptyArrayList.add("item");
        Assertions.assertFalse(Utils.isEmpty(emptyArrayList));
    }
}
