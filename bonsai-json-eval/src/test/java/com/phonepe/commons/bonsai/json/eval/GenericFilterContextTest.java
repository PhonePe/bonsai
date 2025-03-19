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

    @BeforeEach
    void setUp() {
        mockFilter = Mockito.mock(GenericFilter.class);
        mockContext = Mockito.mock(JsonEvalContext.class);
        DocumentContext mockDocumentContext = Mockito.mock(DocumentContext.class);
        Mockito.when(mockContext.documentContext()).thenReturn(mockDocumentContext);
    }

    @Test
    void testGenericFilterContextCreation() {
        GenericFilterContext<JsonEvalContext> context = new GenericFilterContext<>(mockFilter, mockContext);
        Assertions.assertEquals(mockFilter, context.getGenericFilter());
        Assertions.assertEquals(mockContext, context.getContext());
    }

    @Test
    void testGenericFilterContextSetters() {
        GenericFilterContext<JsonEvalContext> context = new GenericFilterContext<>(mockFilter, mockContext);
        
        GenericFilter newFilter = Mockito.mock(GenericFilter.class);
        JsonEvalContext newContext = Mockito.mock(JsonEvalContext.class);
        
        context.setGenericFilter(newFilter);
        context.setContext(newContext);
        
        Assertions.assertEquals(newFilter, context.getGenericFilter());
        Assertions.assertEquals(newContext, context.getContext());
    }

    @Test
    void testGenericFilterContextEquality() {
        GenericFilterContext<JsonEvalContext> context1 = new GenericFilterContext<>(mockFilter, mockContext);
        GenericFilterContext<JsonEvalContext> context2 = new GenericFilterContext<>(mockFilter, mockContext);
        
        GenericFilter differentFilter = Mockito.mock(GenericFilter.class);
        JsonEvalContext differentContext = Mockito.mock(JsonEvalContext.class);
        
        GenericFilterContext<JsonEvalContext> context3 = new GenericFilterContext<>(differentFilter, mockContext);
        GenericFilterContext<JsonEvalContext> context4 = new GenericFilterContext<>(mockFilter, differentContext);
        
        Assertions.assertEquals(context1, context2);
        Assertions.assertNotEquals(context1, context3);
        Assertions.assertNotEquals(context1, context4);
    }

    @Test
    void testGenericFilterContextHashCode() {
        GenericFilterContext<JsonEvalContext> context1 = new GenericFilterContext<>(mockFilter, mockContext);
        GenericFilterContext<JsonEvalContext> context2 = new GenericFilterContext<>(mockFilter, mockContext);
        
        Assertions.assertEquals(context1.hashCode(), context2.hashCode());
    }

    @Test
    void testGenericFilterContextToString() {
        GenericFilterContext<JsonEvalContext> context = new GenericFilterContext<>(mockFilter, mockContext);
        String toString = context.toString();
        
        Assertions.assertNotNull(toString);
        Assertions.assertTrue(toString.contains("genericFilter"));
        Assertions.assertTrue(toString.contains("context"));
    }
}
