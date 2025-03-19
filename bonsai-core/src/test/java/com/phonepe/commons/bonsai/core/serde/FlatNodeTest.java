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

package com.phonepe.commons.bonsai.core.serde;

import com.phonepe.commons.bonsai.core.ObjectExtractor;
import com.phonepe.commons.bonsai.models.model.FlatNode;
import com.phonepe.commons.bonsai.models.model.ValueFlatNode;
import com.phonepe.commons.bonsai.models.value.BooleanValue;
import com.phonepe.commons.bonsai.models.value.JsonValue;
import com.phonepe.commons.bonsai.models.value.NumberValue;
import com.phonepe.commons.bonsai.models.value.StringValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class FlatNodeTest {
    private final ObjectExtractor objectExtractor = new ObjectExtractor();

    @Test
    void testJsonValueFlatNode() throws IOException {
        FlatNode flatNode = objectExtractor.getObject("serde/jsonValue.json", FlatNode.class);
        Assertions.assertEquals(FlatNode.FlatNodeType.VALUE, flatNode.getType());
        Assertions.assertEquals("json",
                ((JsonValue) ((ValueFlatNode) flatNode).getValue()).getValue().get("some").asText());
    }

    @Test
    void testNumberValueFlatNode() throws IOException {
        FlatNode flatNode = objectExtractor.getObject("serde/numberValue.json", FlatNode.class);
        Assertions.assertEquals(FlatNode.FlatNodeType.VALUE, flatNode.getType());
        Assertions.assertEquals(2, ((NumberValue) ((ValueFlatNode) flatNode).getValue()).getValue());
    }

    @Test
    void testStringValueFlatNode() throws IOException {
        FlatNode flatNode = objectExtractor.getObject("serde/stringValue.json", FlatNode.class);
        Assertions.assertEquals(FlatNode.FlatNodeType.VALUE, flatNode.getType());
        Assertions.assertEquals("hello", ((StringValue) ((ValueFlatNode) flatNode).getValue()).getValue());
    }

    @Test
    void testBooleanValueFlatNode() throws IOException {
        FlatNode flatNode = objectExtractor.getObject("serde/booleanValue.json", FlatNode.class);
        Assertions.assertEquals(FlatNode.FlatNodeType.VALUE, flatNode.getType());
        Assertions.assertEquals(true, ((BooleanValue) ((ValueFlatNode) flatNode).getValue()).isValue());
    }
}