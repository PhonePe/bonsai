package com.phonepe.platform.bonsai.json.eval;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.TypeRef;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class PathExpressionTest {

    @Test
    public void testJsonPath() throws Exception {
        JsonPathSetup.setup();
        DocumentContext parse = JsonPath.parse("{\n" +
                "  \"t\": [\n" +
                "    {\n" +
                "      \"price\": 1,\n" +
                "      \"name\": \"anton\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"price\": 10,\n" +
                "      \"name\": \"anaton\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"price\": 120,\n" +
                "      \"name\": \"aanton\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"price\": 10,\n" +
                "      \"name\": \"aaanton\"\n" +
                "    }\n" +
                "  ]\n" +
                "}");

        val read = parse.read("$.t[*].price", new TypeRef<List<Integer>>() {
                })
                .stream()
                .mapToInt(Integer::intValue)
                .sum();
        val read2 = parse.read("$.t[*].price", new TypeRef<List<Integer>>() {
                })
                .stream()
                .mapToInt(Integer::intValue)
                .sum();
        Assert.assertEquals(141, read);
        Assert.assertEquals(141, read2);

    }

}