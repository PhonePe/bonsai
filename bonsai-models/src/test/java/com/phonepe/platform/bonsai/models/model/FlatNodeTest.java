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
        FlatNode flatNode = objectMapper.readValue("""
                {
                	"type": "VALUE",\s
                	"value": {
                		"valueType": "NUMBER",
                		"value": 2
                	}
                }\
                """, FlatNode.class);
        Assertions.assertEquals(FlatNode.FlatNodeType.VALUE, flatNode.getType());
    }
}