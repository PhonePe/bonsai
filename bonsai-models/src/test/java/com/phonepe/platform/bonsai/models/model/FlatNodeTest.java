package com.phonepe.platform.bonsai.models.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class FlatNodeTest {
    @Test
    void name() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new ParameterNamesModule());
        FlatNode flatNode = objectMapper.readValue("{\n" +
                "\t\"type\": \"VALUE\", \n" +
                "\t\"value\": {\n" +
                "\t\t\"valueType\": \"NUMBER\",\n" +
                "\t\t\"value\": 2\n" +
                "\t}\n" +
                "}", FlatNode.class);
        Assertions.assertEquals(FlatNode.FlatNodeType.VALUE, flatNode.getType());
    }
}