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
import com.phonepe.commons.query.dsl.Filter;
import com.phonepe.commons.query.dsl.general.EqualsFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

public class PathExpressionTest {

    private DocumentContext mockDocumentContext;

    @BeforeEach
    void setUp() {
        JsonPathSetup.setup();
        mockDocumentContext = Mockito.mock(DocumentContext.class);
    }

    @Test
    void testJsonPath() {
        DocumentContext parse = Parsers.parse("""
                {
                  "t": [
                    {
                      "price": 1,
                      "name": "anton"
                    },
                    {
                      "price": 10,
                      "name": "anaton"
                    },
                    {
                      "price": 120,
                      "name": "aanton"
                    },
                    {
                      "price": 10,
                      "name": "aaanton"
                    }
                  ]
                }\
                """);

        final var read = parse.read("$.t[*].price", new TypeRef<List<Integer>>() {
                })
                .stream()
                .mapToInt(Integer::intValue)
                .sum();
        final var read2 = parse.read("$.t[*].price", new TypeRef<List<Integer>>() {
                })
                .stream()
                .mapToInt(Integer::intValue)
                .sum();
        Assertions.assertEquals(141, read);
        Assertions.assertEquals(141, read2);
    }

    @Test
    void testEvalWithSimplePath() {
        PathExpression expression = new PathExpression();
        expression.setKey("testKey");
        expression.setPath("$.data.value");

        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList(42));

        Pair<String, Object> result = expression.eval(mockDocumentContext);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("testKey", result.getKey());
        Assertions.assertEquals(42, result.getValue());
    }

    @Test
    void testEvalWithMultivaluedPath() {
        PathExpression expression = new PathExpression();
        expression.setKey("testKey");
        expression.setPath("$.data.values");
        expression.setMultivalued(true);

        List<Integer> values = Arrays.asList(1, 2, 3);
        Mockito.when(mockDocumentContext.read(eq("$.data.values"), any(TypeRef.class)))
                .thenReturn(values);

        Pair<String, Object> result = expression.eval(mockDocumentContext);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("testKey", result.getKey());
        Assertions.assertEquals(values, result.getValue());
    }

    @Test
    void testEvalWithNullValues() {
        PathExpression expression = new PathExpression();
        expression.setKey("testKey");
        expression.setPath("$.data.value");

        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(null);

        Pair<String, Object> result = expression.eval(mockDocumentContext);
        Assertions.assertNull(result);
    }

    @Test
    void testEvalWithEmptyValues() {
        PathExpression expression = new PathExpression();
        expression.setKey("testKey");
        expression.setPath("$.data.value");

        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.emptyList());

        Pair<String, Object> result = expression.eval(mockDocumentContext);
        Assertions.assertNull(result);
    }

    @Test
    void testEvalWithSumOperation() {
        PathExpression expression = new PathExpression();
        expression.setKey("sum");
        expression.setPath("$.data.values");
        expression.setOperation(PathExpression.Operation.SUM);

        List<Number> values = Arrays.asList(1, 2, 3, 4, 5);
        Mockito.when(mockDocumentContext.read(eq("$.data.values"), any(TypeRef.class)))
                .thenReturn(values);

        Pair<String, Object> result = expression.eval(mockDocumentContext);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("sum", result.getKey());
        Assertions.assertEquals(15.0, result.getValue());
    }

    @Test
    void testEvalWithAverageOperation() {
        PathExpression expression = new PathExpression();
        expression.setKey("avg");
        expression.setPath("$.data.values");
        expression.setOperation(PathExpression.Operation.AVERAGE);

        List<Number> values = Arrays.asList(1, 2, 3, 4, 5);
        Mockito.when(mockDocumentContext.read(eq("$.data.values"), any(TypeRef.class)))
                .thenReturn(values);

        Pair<String, Object> result = expression.eval(mockDocumentContext);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("avg", result.getKey());
        Assertions.assertEquals(3.0, result.getValue());
    }

    @Test
    void testEvalWithMaxOperation() {
        PathExpression expression = new PathExpression();
        expression.setKey("max");
        expression.setPath("$.data.values");
        expression.setOperation(PathExpression.Operation.MAX);

        List<Number> values = Arrays.asList(1, 2, 3, 4, 5);
        Mockito.when(mockDocumentContext.read(eq("$.data.values"), any(TypeRef.class)))
                .thenReturn(values);

        Pair<String, Object> result = expression.eval(mockDocumentContext);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("max", result.getKey());
        Assertions.assertEquals(5.0, result.getValue());
    }

    @Test
    void testEvalWithMinOperation() {
        PathExpression expression = new PathExpression();
        expression.setKey("min");
        expression.setPath("$.data.values");
        expression.setOperation(PathExpression.Operation.MIN);

        List<Number> values = Arrays.asList(1, 2, 3, 4, 5);
        Mockito.when(mockDocumentContext.read(eq("$.data.values"), any(TypeRef.class)))
                .thenReturn(values);

        Pair<String, Object> result = expression.eval(mockDocumentContext);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("min", result.getKey());
        Assertions.assertEquals(1.0, result.getValue());
    }

    @Test
    void testEvalWithLengthOperation() {
        PathExpression expression = new PathExpression();
        expression.setKey("length");
        expression.setPath("$.data.values");
        expression.setOperation(PathExpression.Operation.LENGTH);

        List<Number> values = Arrays.asList(1, 2, 3, 4, 5);
        Mockito.when(mockDocumentContext.read(eq("$.data.values"), any(TypeRef.class)))
                .thenReturn(values);

        Pair<String, Object> result = expression.eval(mockDocumentContext);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("length", result.getKey());
        Assertions.assertEquals(5.0, result.getValue());
    }

    @Test
    void testEvalWithPadTimestampOperation() {
        PathExpression expression = new PathExpression();
        expression.setKey("timestamp");
        expression.setPath("$.data.timestamp");
        expression.setOperation(PathExpression.Operation.PAD_TIMESTAMP);

        List<Number> values = Collections.singletonList(12345);
        Mockito.when(mockDocumentContext.read(eq("$.data.timestamp"), any(TypeRef.class)))
                .thenReturn(values);

        Pair<String, Object> result = expression.eval(mockDocumentContext);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("timestamp", result.getKey());
        Assertions.assertEquals("00000000000000012345", result.getValue());
    }

    @Test
    void testEvalWithConvertToDateOperation() {
        PathExpression expression = new PathExpression();
        expression.setKey("date");
        expression.setPath("$.data.timestamp");
        expression.setOperation(PathExpression.Operation.CONVERT_TO_DATE);

        long timestamp = 1609459200000L; // 2021-01-01 00:00:00 UTC
        List<Number> values = Collections.singletonList(timestamp);
        Mockito.when(mockDocumentContext.read(eq("$.data.timestamp"), any(TypeRef.class)))
                .thenReturn(values);

        Pair<String, Object> result = expression.eval(mockDocumentContext);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("date", result.getKey());
        Assertions.assertTrue(result.getValue() instanceof Date);
        Assertions.assertEquals(timestamp, ((Date) result.getValue()).getTime());
    }

    @Test
    void testEvalWithAdjustments() {
        PathExpression expression = new PathExpression();
        expression.setKey("adjusted");
        expression.setPath("$.data.value");

        List<PathExpression.Adjustment> adjustments = new ArrayList<>();
        PathExpression.Adjustment adjustment1 = new PathExpression.Adjustment();
        adjustment1.type = PathExpression.Adjustment.Type.ADD;
        adjustment1.value = 10;
        adjustments.add(adjustment1);

        PathExpression.Adjustment adjustment2 = new PathExpression.Adjustment();
        adjustment2.type = PathExpression.Adjustment.Type.MULTIPLY;
        adjustment2.value = 2;
        adjustments.add(adjustment2);

        expression.setAdjustments(adjustments);

        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList(5));

        Pair<String, Object> result = expression.eval(mockDocumentContext);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("adjusted", result.getKey());
        // (5 + 10) * 2 = 30
        Assertions.assertEquals(30.0, result.getValue());
    }

    @Test
    void testEvalWithFilters() {
        PathExpression expression = new PathExpression();
        expression.setKey("filtered");
        expression.setPath("$.data.value");
        
        EqualsFilter filter = new EqualsFilter();
        filter.setField("$.transaction");
        filter.setValue("tid01");
        expression.setFilters(Collections.singletonList(filter));

        // Mock the filter evaluation to return true
        Mockito.when(mockDocumentContext.read(anyString(), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("tid01"));
        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList(42));


        Pair<String, Object> result = expression.eval(mockDocumentContext);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("filtered", result.getKey());
        Assertions.assertEquals(42, result.getValue());
    }

    @Test
    void testEvalWithFiltersNotMatching() {
        PathExpression expression = new PathExpression();
        expression.setKey("filtered");
        expression.setPath("$.data.value");
        
        EqualsFilter filter = new EqualsFilter();
        filter.setField("$.transaction");
        filter.setValue("tid01");
        expression.setFilters(Collections.singletonList(filter));

        Mockito.when(mockDocumentContext.read(eq("$.data.value"), any(TypeRef.class)))
                .thenReturn(Collections.singletonList(42));
        
        // Mock the filter evaluation to return false (different value)
        Mockito.when(mockDocumentContext.read(anyString(), any(TypeRef.class)))
                .thenReturn(Collections.singletonList("tid02"));

        Pair<String, Object> result = expression.eval(mockDocumentContext);
        Assertions.assertNull(result);
    }

    @Test
    void testEvalWithDirectValue() {
        PathExpression expression = new PathExpression();
        expression.setKey("directValue");
        expression.setValue("testValue");

        Pair<String, Object> result = expression.eval(mockDocumentContext);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("directValue", result.getKey());
        Assertions.assertEquals("testValue", result.getValue());
    }

    @Test
    void testToString() {
        PathExpression expression = new PathExpression();
        expression.setKey("testKey");
        expression.setPath("$.data.value");
        expression.setOperation(PathExpression.Operation.SUM);

        String toString = expression.toString();
        Assertions.assertTrue(toString.contains("testKey"));
        Assertions.assertTrue(toString.contains("$.data.value"));
        Assertions.assertTrue(toString.contains("SUM"));
    }
}
