package com.phonepe.platform.bonsai.json.eval;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.TypeRef;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class PathExpressionTest {

    @Test
    void testJsonPath() throws Exception {
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