package com.phonepe.platform.bonsai.models.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * @author tushar.naik
 * @version 1.0  2019-06-17 - 17:43
 */
public class FlatNodeTest {
    @Test
    public void name() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new ParameterNamesModule());
        FlatNode flatNode = objectMapper.readValue("{\n" +
                                                                 "\t\"type\": \"VALUE\", \n" +
                                                                 "\t\"value\": {\n" +
                                                                 "\t\t\"valueType\": \"NUMBER\",\n" +
                                                                 "\t\t\"value\": 2\n" +
                                                                 "\t}\n" +
                                                                 "}", FlatNode.class);
        Assert.assertEquals(FlatNode.FlatNodeType.VALUE, flatNode.getType());
    }
}