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
import com.jayway.jsonpath.TypeRef;
import com.phonepe.commons.query.dsl.general.AnyFilter;
import com.phonepe.commons.query.dsl.general.ContainsFilter;
import com.phonepe.commons.query.dsl.general.EqualsFilter;
import com.phonepe.commons.query.dsl.general.ExistsFilter;
import com.phonepe.commons.query.dsl.general.GenericFilter;
import com.phonepe.commons.query.dsl.general.InFilter;
import com.phonepe.commons.query.dsl.general.MissingFilter;
import com.phonepe.commons.query.dsl.general.NotEqualsFilter;
import com.phonepe.commons.query.dsl.general.NotInFilter;
import com.phonepe.commons.query.dsl.logical.AndFilter;
import com.phonepe.commons.query.dsl.logical.NotFilter;
import com.phonepe.commons.query.dsl.logical.OrFilter;
import com.phonepe.commons.query.dsl.numeric.BetweenFilter;
import com.phonepe.commons.query.dsl.numeric.GreaterEqualFilter;
import com.phonepe.commons.query.dsl.numeric.GreaterThanFilter;
import com.phonepe.commons.query.dsl.numeric.LessEqualFilter;
import com.phonepe.commons.query.dsl.numeric.LessThanFilter;
import com.phonepe.commons.query.dsl.string.StringEndsWithFilter;
import com.phonepe.commons.query.dsl.string.StringRegexMatchFilter;
import com.phonepe.commons.query.dsl.string.StringStartsWithFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Predicate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class TraceWrappedJsonPathFilterEvaluationEngineTest {

    private DocumentContext mockDocumentContext;
    private JsonEvalContext mockContext;
    private Predicate<GenericFilterContext<JsonEvalContext>> mockGenericFilterHandler;
    private TraceWrappedJsonPathFilterEvaluationEngine<JsonEvalContext> engine;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        JsonPathSetup.setup();
        mockDocumentContext = Mockito.mock(DocumentContext.class);
        mockContext = Mockito.mock(JsonEvalContext.class);
        mockGenericFilterHandler = Mockito.mock(Predicate.class);
        
        Mockito.when(mockContext.documentContext()).thenReturn(mockDocumentContext);
        Mockito.when(mockContext.id()).thenReturn("test-id");
        
        engine = new TraceWrappedJsonPathFilterEvaluationEngine<>("test-entity", mockContext, mockGenericFilterHandler);
    }

    @Test
    void testVisitContainsFilter() {
        ContainsFilter filter = new ContainsFilter();
        filter.setField("$.data.text");
        filter.setValue("test");
        
        Mockito.when(mockDocumentContext.read(eq("$.data.text"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("This is a test string"));
        
        Boolean result = engine.visit(filter);
        Assertions.assertTrue(result);
    }

    @Test
    void testVisitLessThanFilter() {
        LessThanFilter filter = new LessThanFilter();
        filter.setField("$.data.value");
        filter.setValue(10);
        
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList(5));
        
        Boolean result = engine.visit(filter);
        Assertions.assertTrue(result);
    }

    @Test
    void testVisitLessEqualFilter() {
        LessEqualFilter filter = new LessEqualFilter();
        filter.setField("$.data.value");
        filter.setValue(10);
        
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList(10));
        
        Boolean result = engine.visit(filter);
        Assertions.assertTrue(result);
    }

    @Test
    void testVisitGreaterThanFilter() {
        GreaterThanFilter filter = new GreaterThanFilter();
        filter.setField("$.data.value");
        filter.setValue(10);
        
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList(15));
        
        Boolean result = engine.visit(filter);
        Assertions.assertTrue(result);
    }

    @Test
    void testVisitGreaterEqualFilter() {
        GreaterEqualFilter filter = new GreaterEqualFilter();
        filter.setField("$.data.value");
        filter.setValue(10);
        
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList(10));
        
        Boolean result = engine.visit(filter);
        Assertions.assertTrue(result);
    }

    @Test
    void testVisitBetweenFilter() {
        BetweenFilter filter = new BetweenFilter();
        filter.setField("$.data.value");
        filter.setFrom(10);
        filter.setTo(20);
        
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList(15));
        
        Boolean result = engine.visit(filter);
        Assertions.assertTrue(result);
    }

    @Test
    void testVisitNotInFilter() {
        NotInFilter filter = new NotInFilter();
        filter.setField("$.data.value");
        filter.setValues(Arrays.asList(1, 2, 3));
        
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList(5));
        
        Boolean result = engine.visit(filter);
        Assertions.assertTrue(result);
    }

    @Test
    void testVisitNotEqualsFilter() {
        NotEqualsFilter filter = new NotEqualsFilter();
        filter.setField("$.data.value");
        filter.setValue("test");
        
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("other"));
        
        Boolean result = engine.visit(filter);
        Assertions.assertTrue(result);
    }

    @Test
    void testVisitMissingFilter() {
        MissingFilter filter = new MissingFilter();
        filter.setField("$.data.value");
        
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(null);
        
        Boolean result = engine.visit(filter);
        Assertions.assertTrue(result);
    }

    @Test
    void testVisitInFilter() {
        InFilter filter = new InFilter();
        filter.setField("$.data.value");
        filter.setValues(Arrays.asList(1, 2, 3));
        
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList(2));
        
        Boolean result = engine.visit(filter);
        Assertions.assertTrue(result);
    }

    @Test
    void testVisitExistsFilter() {
        ExistsFilter filter = new ExistsFilter();
        filter.setField("$.data.value");
        
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("test"));
        
        Boolean result = engine.visit(filter);
        Assertions.assertTrue(result);
    }

    @Test
    void testVisitEqualsFilter() {
        EqualsFilter filter = new EqualsFilter();
        filter.setField("$.data.value");
        filter.setValue("test");
        
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("test"));
        
        Boolean result = engine.visit(filter);
        Assertions.assertTrue(result);
    }

    @Test
    void testVisitAnyFilter() {
        AnyFilter filter = new AnyFilter();
        
        Boolean result = engine.visit(filter);
        Assertions.assertTrue(result);
    }

    @Test
    void testVisitAndFilter() {
        EqualsFilter filter1 = new EqualsFilter();
        filter1.setField("$.data.value1");
        filter1.setValue("test1");
        
        EqualsFilter filter2 = new EqualsFilter();
        filter2.setField("$.data.value2");
        filter2.setValue("test2");
        
        AndFilter andFilter = new AndFilter(Arrays.asList(filter1, filter2));
        
        Mockito.when(mockDocumentContext.read(eq("$.data.value1"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("test1"));
        Mockito.when(mockDocumentContext.read(eq("$.data.value2"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("test2"));
        
        Boolean result = engine.visit(andFilter);
        Assertions.assertTrue(result);
    }

    @Test
    void testVisitOrFilter() {
        EqualsFilter filter1 = new EqualsFilter();
        filter1.setField("$.data.value1");
        filter1.setValue("test1");
        
        EqualsFilter filter2 = new EqualsFilter();
        filter2.setField("$.data.value2");
        filter2.setValue("test2");
        
        OrFilter orFilter = new OrFilter(Arrays.asList(filter1, filter2));
        
        Mockito.when(mockDocumentContext.read(eq("$.data.value1"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("test1"));
        
        Boolean result = engine.visit(orFilter);
        Assertions.assertTrue(result);
    }

    @Test
    void testVisitNotFilter() {
        EqualsFilter equalsFilter = new EqualsFilter();
        equalsFilter.setField("$.data.value");
        equalsFilter.setValue("test");
        
        NotFilter notFilter = new NotFilter(equalsFilter);
        
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("other"));
        
        Boolean result = engine.visit(notFilter);
        Assertions.assertTrue(result);
    }

    @Test
    void testVisitStringStartsWithFilter() {
        StringStartsWithFilter filter = new StringStartsWithFilter();
        filter.setField("$.data.text");
        filter.setValue("test");
        
        Mockito.when(mockDocumentContext.read(eq("$.data.text"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("test string"));
        
        Boolean result = engine.visit(filter);
        Assertions.assertTrue(result);
    }

    @Test
    void testVisitStringEndsWithFilter() {
        StringEndsWithFilter filter = new StringEndsWithFilter();
        filter.setField("$.data.text");
        filter.setValue("test");
        
        Mockito.when(mockDocumentContext.read(eq("$.data.text"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("string test"));
        
        Boolean result = engine.visit(filter);
        Assertions.assertTrue(result);
    }

    @Test
    void testVisitStringRegexMatchFilter() {
        StringRegexMatchFilter filter = new StringRegexMatchFilter();
        filter.setField("$.data.text");
        filter.setValue("test\\d+");
        
        Mockito.when(mockDocumentContext.read(eq("$.data.text"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("test123"));
        
        Boolean result = engine.visit(filter);
        Assertions.assertTrue(result);
    }

    @Test
    void testVisitGenericFilter() {
        GenericFilter filter = Mockito.mock(GenericFilter.class);
        
        Mockito.when(mockGenericFilterHandler.test(any(GenericFilterContext.class)))
                .thenReturn(true);
        
        Boolean result = engine.visit(filter);
        Assertions.assertTrue(result);
    }

    @Test
    void testTraceWrappedVsRegularEngine() {
        // Create both engines with the same parameters
        JsonPathFilterEvaluationEngine<JsonEvalContext> regularEngine = 
                new JsonPathFilterEvaluationEngine<>("test-entity", mockContext, mockGenericFilterHandler);
        
        // Test that both engines return the same result for the same input
        EqualsFilter filter = new EqualsFilter();
        filter.setField("$.data.value");
        filter.setValue("test");
        
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("test"));
        
        Boolean regularResult = regularEngine.visit(filter);
        Boolean traceResult = engine.visit(filter);
        
        Assertions.assertEquals(regularResult, traceResult);
    }
}
