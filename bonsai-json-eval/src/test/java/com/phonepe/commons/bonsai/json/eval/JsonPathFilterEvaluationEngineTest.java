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
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

public class JsonPathFilterEvaluationEngineTest {

    private DocumentContext mockDocumentContext;
    private JsonEvalContext mockContext;
    private Predicate<GenericFilterContext<JsonEvalContext>> mockGenericFilterHandler;
    private JsonPathFilterEvaluationEngine<JsonEvalContext> engine;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        JsonPathSetup.setup();
        mockDocumentContext = Mockito.mock(DocumentContext.class);
        mockContext = Mockito.mock(JsonEvalContext.class);
        mockGenericFilterHandler = Mockito.mock(Predicate.class);
        
        Mockito.when(mockContext.documentContext()).thenReturn(mockDocumentContext);
        Mockito.when(mockContext.id()).thenReturn("test-id");
        
        engine = new JsonPathFilterEvaluationEngine<>("test-entity", mockContext, mockGenericFilterHandler);
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
        
        // Test with non-matching value
        Mockito.when(mockDocumentContext.read(eq("$.data.text"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("This is a string"));
        
        result = engine.visit(filter);
        Assertions.assertFalse(result);
    }

    @Test
    void testVisitContainsFilterWithIterable() {
        ContainsFilter filter = new ContainsFilter();
        filter.setField("$.data.tags");
        filter.setValue("test");
        filter.setIterable(true);

        final var iterables = List.of(List.of("tag1", "test", "tag3"));
        Mockito.when(mockDocumentContext.read(eq("$.data.tags"), any(TypeRef.class)))
                .thenReturn(iterables);

        Boolean result = engine.visit(filter);
        Assertions.assertTrue(result);
        
        // Test with non-matching value
        Mockito.when(mockDocumentContext.read(eq("$.data.tags"), any(TypeRef.class)))
                .thenReturn(List.of(List.of("tag1", "tag2", "tag3")));
        
        result = engine.visit(filter);
        Assertions.assertFalse(result);
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
        
        // Test with non-matching value
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList(15));
        
        result = engine.visit(filter);
        Assertions.assertFalse(result);
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
        
        // Test with non-matching value
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList(11));
        
        result = engine.visit(filter);
        Assertions.assertFalse(result);
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
        
        // Test with non-matching value
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList(5));
        
        result = engine.visit(filter);
        Assertions.assertFalse(result);
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
        
        // Test with non-matching value
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList(9));
        
        result = engine.visit(filter);
        Assertions.assertFalse(result);
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
        
        // Test with value at lower bound (exclusive)
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList(10));
        
        result = engine.visit(filter);
        Assertions.assertFalse(result);
        
        // Test with value at upper bound (exclusive)
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList(20));
        
        result = engine.visit(filter);
        Assertions.assertFalse(result);
        
        // Test with value outside range
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList(5));
        
        result = engine.visit(filter);
        Assertions.assertFalse(result);
    }

    @Test
    void testVisitNotInFilter() {
        NotInFilter filter = new NotInFilter();
        filter.setField("$.data.value");
        filter.setValues(Set.of(1, 2, 3));
        
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList(5));
        
        Boolean result = engine.visit(filter);
        Assertions.assertTrue(result);
        
        // Test with matching value
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList(2));
        
        result = engine.visit(filter);
        Assertions.assertFalse(result);
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
        
        // Test with matching value
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("test"));
        
        result = engine.visit(filter);
        Assertions.assertFalse(result);
    }

    @Test
    void testVisitMissingFilter() {
        MissingFilter filter = new MissingFilter();
        filter.setField("$.data.value");
        
        // Test with null value
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(null);
        
        Boolean result = engine.visit(filter);
        Assertions.assertTrue(result);
        
        // Test with empty list
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.emptyList());
        
        result = engine.visit(filter);
        Assertions.assertTrue(result);
        
        // Test with list containing null
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList(null));
        
        result = engine.visit(filter);
        Assertions.assertTrue(result);
        
        // Test with non-null value
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("test"));
        
        result = engine.visit(filter);
        Assertions.assertFalse(result);
    }

    @Test
    void testVisitInFilter() {
        InFilter filter = new InFilter();
        filter.setField("$.data.value");
        filter.setValues(Set.of(1, 2, 3));
        
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList(2));
        
        Boolean result = engine.visit(filter);
        Assertions.assertTrue(result);
        
        // Test with non-matching value
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList(5));
        
        result = engine.visit(filter);
        Assertions.assertFalse(result);
        
        // Test with null value
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList(null));
        
        result = engine.visit(filter);
        Assertions.assertFalse(result);
        
        // Test with empty list
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.emptyList());
        
        result = engine.visit(filter);
        Assertions.assertFalse(result);
    }

    @Test
    void testVisitExistsFilter() {
        ExistsFilter filter = new ExistsFilter();
        filter.setField("$.data.value");
        
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("test"));
        
        Boolean result = engine.visit(filter);
        Assertions.assertTrue(result);
        
        // Test with null value
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(null);
        
        result = engine.visit(filter);
        Assertions.assertFalse(result);
        
        // Test with empty list
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.emptyList());
        
        result = engine.visit(filter);
        Assertions.assertFalse(result);
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
        
        // Test with non-matching value
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("other"));
        
        result = engine.visit(filter);
        Assertions.assertFalse(result);
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
        
        // Both filters match
        Mockito.when(mockDocumentContext.read(eq("$.data.value1"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("test1"));
        Mockito.when(mockDocumentContext.read(eq("$.data.value2"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("test2"));
        
        Boolean result = engine.visit(andFilter);
        Assertions.assertTrue(result);
        
        // One filter doesn't match
        Mockito.when(mockDocumentContext.read(eq("$.data.value1"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("test1"));
        Mockito.when(mockDocumentContext.read(eq("$.data.value2"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("other"));
        
        result = engine.visit(andFilter);
        Assertions.assertFalse(result);
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
        
        // Both filters match
        Mockito.when(mockDocumentContext.read(eq("$.data.value1"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("test1"));
        Mockito.when(mockDocumentContext.read(eq("$.data.value2"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("test2"));
        
        Boolean result = engine.visit(orFilter);
        Assertions.assertTrue(result);
        
        // One filter matches
        Mockito.when(mockDocumentContext.read(eq("$.data.value1"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("test1"));
        Mockito.when(mockDocumentContext.read(eq("$.data.value2"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("other"));
        
        result = engine.visit(orFilter);
        Assertions.assertTrue(result);
        
        // No filter matches
        Mockito.when(mockDocumentContext.read(eq("$.data.value1"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("other1"));
        Mockito.when(mockDocumentContext.read(eq("$.data.value2"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("other2"));
        
        result = engine.visit(orFilter);
        Assertions.assertFalse(result);
    }

    @Test
    void testVisitNotFilter() {
        EqualsFilter equalsFilter = new EqualsFilter();
        equalsFilter.setField("$.data.value");
        equalsFilter.setValue("test");
        
        NotFilter notFilter = new NotFilter(equalsFilter);
        
        // Equals filter matches
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("test"));
        
        Boolean result = engine.visit(notFilter);
        Assertions.assertFalse(result);
        
        // Equals filter doesn't match
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("other"));
        
        result = engine.visit(notFilter);
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
        
        // Test with non-matching value
        Mockito.when(mockDocumentContext.read(eq("$.data.text"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("string test"));
        
        result = engine.visit(filter);
        Assertions.assertFalse(result);
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
        
        // Test with non-matching value
        Mockito.when(mockDocumentContext.read(eq("$.data.text"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("test string"));
        
        result = engine.visit(filter);
        Assertions.assertFalse(result);
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
        
        // Test with non-matching value
        Mockito.when(mockDocumentContext.read(eq("$.data.text"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("test"));
        
        result = engine.visit(filter);
        Assertions.assertFalse(result);
    }

    @Test
    void testVisitGenericFilter() {
        GenericFilter filter = Mockito.mock(GenericFilter.class);
        
        Mockito.when(mockGenericFilterHandler.test(any(GenericFilterContext.class)))
                .thenReturn(true);
        
        Boolean result = engine.visit(filter);
        Assertions.assertTrue(result);
        
        // Test with handler returning false
        Mockito.when(mockGenericFilterHandler.test(any(GenericFilterContext.class)))
                .thenReturn(false);
        
        result = engine.visit(filter);
        Assertions.assertFalse(result);
    }

    @Test
    void testWithNullValues() {
        EqualsFilter filter = new EqualsFilter();
        filter.setField("$.data.value");
        filter.setValue("test");
        
        // Test with null return from read
        Mockito.when(mockDocumentContext.read(anyString(), any(TypeRef.class)))
                .thenReturn(null);
        
        Boolean result = engine.visit(filter);
        Assertions.assertFalse(result);
    }

    @Test
    void testWithEmptyValues() {
        EqualsFilter filter = new EqualsFilter();
        filter.setField("$.data.value");
        filter.setValue("test");
        
        // Test with empty list return from read
        Mockito.when(mockDocumentContext.read(anyString(), any(TypeRef.class)))
                .thenReturn(Collections.emptyList());
        
        Boolean result = engine.visit(filter);
        Assertions.assertFalse(result);
    }

    @Test
    void testWithNullValueInList() {
        EqualsFilter filter = new EqualsFilter();
        filter.setField("$.data.value");
        filter.setValue("test");
        
        // Test with list containing null
        List<String> listWithNull = Arrays.asList(null, "test");
        Mockito.when(mockDocumentContext.read(anyString(), any(TypeRef.class)))
                .thenReturn(listWithNull);
        
        Boolean result = engine.visit(filter);
        Assertions.assertTrue(result);
    }
}
