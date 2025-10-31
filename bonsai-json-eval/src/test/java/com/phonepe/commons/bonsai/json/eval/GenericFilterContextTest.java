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

import com.jayway.jsonpath.DocumentContext;
import com.phonepe.commons.query.dsl.general.GenericFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class GenericFilterContextTest {

    private GenericFilter mockFilter;
    private JsonEvalContext mockContext;
    private String entityMetadata;

    @BeforeEach
    void setUp() {
        mockFilter = Mockito.mock(GenericFilter.class);
        mockContext = Mockito.mock(JsonEvalContext.class);
        entityMetadata = "test-key";

        DocumentContext mockDocumentContext = Mockito.mock(DocumentContext.class);
        Mockito.when(mockContext.documentContext()).thenReturn(mockDocumentContext);
    }

    @Test
    void testGenericFilterContextCreation() {
        GenericFilterContext<JsonEvalContext, String> context = new GenericFilterContext<>(mockFilter, mockContext, entityMetadata);
        Assertions.assertEquals(mockFilter, context.getGenericFilter());
        Assertions.assertEquals(mockContext, context.getContext());
        Assertions.assertEquals(entityMetadata, context.getEntityMetadata());
    }

    @Test
    void testGenericFilterContextSetters() {
        GenericFilterContext<JsonEvalContext, String> context = new GenericFilterContext<>(mockFilter, mockContext, entityMetadata);
        
        GenericFilter newFilter = Mockito.mock(GenericFilter.class);
        JsonEvalContext newContext = Mockito.mock(JsonEvalContext.class);
        String newEntityMetadata = "new-key";

        context.setGenericFilter(newFilter);
        context.setContext(newContext);
        context.setEntityMetadata(newEntityMetadata);
        
        Assertions.assertEquals(newFilter, context.getGenericFilter());
        Assertions.assertEquals(newContext, context.getContext());
        Assertions.assertEquals(newEntityMetadata, context.getEntityMetadata());
    }

    @Test
    void testGenericFilterContextEquality() {
        GenericFilterContext<JsonEvalContext, String> context1 = new GenericFilterContext<>(mockFilter, mockContext, entityMetadata);
        GenericFilterContext<JsonEvalContext, String> context2 = new GenericFilterContext<>(mockFilter, mockContext, entityMetadata);
        
        GenericFilter differentFilter = Mockito.mock(GenericFilter.class);
        JsonEvalContext differentContext = Mockito.mock(JsonEvalContext.class);
        String differentEntityMetadata = "different-key";

        GenericFilterContext<JsonEvalContext, String> context3 = new GenericFilterContext<>(differentFilter, mockContext,entityMetadata);
        GenericFilterContext<JsonEvalContext, String> context4 = new GenericFilterContext<>(mockFilter, differentContext, entityMetadata);
        GenericFilterContext<JsonEvalContext, String> context5 = new GenericFilterContext<>(mockFilter, mockContext, differentEntityMetadata);
        
        Assertions.assertEquals(context1, context2);
        Assertions.assertNotEquals(context1, context3);
        Assertions.assertNotEquals(context1, context4);
        Assertions.assertNotEquals(context1, context5);
    }

    @Test
    void testGenericFilterContextHashCode() {
        GenericFilterContext<JsonEvalContext, String> context1 = new GenericFilterContext<>(mockFilter, mockContext, entityMetadata);
        GenericFilterContext<JsonEvalContext, String> context2 = new GenericFilterContext<>(mockFilter, mockContext, entityMetadata);
        
        Assertions.assertEquals(context1.hashCode(), context2.hashCode());
    }

    @Test
    void testGenericFilterContextToString() {
        GenericFilterContext<JsonEvalContext, String> context = new GenericFilterContext<>(mockFilter, mockContext, entityMetadata);
        String toString = context.toString();
        
        Assertions.assertNotNull(toString);
        Assertions.assertTrue(toString.contains("genericFilter"));
        Assertions.assertTrue(toString.contains("context"));
        Assertions.assertTrue(toString.contains("entityMetadata"));
    }
}
