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

import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.DocumentContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

public class JsonEvalContextTest {

    private final DocumentContext mockDocumentContext = Mockito.mock(DocumentContext.class);
    private final JsonNode mockJsonNode = Mockito.mock(JsonNode.class);
    private final JsonEvalContext context = Utils.getJsonEvalContext(mockDocumentContext, mockJsonNode);

    @Test
    void testDocumentContextMethod() {
        Assertions.assertEquals(mockDocumentContext, context.documentContext());
    }

    @Test
    void testContextAsJsonNodeMethod() {
        Assertions.assertEquals(mockJsonNode, context.contextAsJsonNode());
    }

    @Test
    void testIdMethod() {
        String id = context.id();
        Assertions.assertNotNull(id);
        
        // Verify that the ID is a valid UUID
        try {
            UUID uuid = UUID.fromString(id);
            Assertions.assertNotNull(uuid);
        } catch (IllegalArgumentException e) {
            Assertions.fail("ID is not a valid UUID: " + id);
        }
    }

    @Test
    void testMultipleCallsToIdReturnDifferentValues() {
        String id1 = context.id();
        String id2 = context.id();
        
        Assertions.assertNotEquals(id1, id2, "Multiple calls to id() should return different UUIDs");
    }
}
