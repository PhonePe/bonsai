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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class PathExpressionTest {

    @Test
    void testJsonPath() {
        JsonPathSetup.setup();
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

}