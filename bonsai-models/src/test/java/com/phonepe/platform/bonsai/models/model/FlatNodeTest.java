package com.phonepe.platform.bonsai.models.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author tushar.naik
 * @version 1.0  2019-06-17 - 17:43
 */
public class FlatNodeTest {
    @Test
    public void name() throws IOException {
        FlatNode flatNode = new ObjectMapper().readValue("{\n" +
                                                                 "\t\"type\": \"VALUE\", \n" +
                                                                 "\t\"value\": {\n" +
                                                                 "\t\t\"valueType\": \"DATA\",\n" +
                                                                 "\t\t\"data\": 2\n" +
                                                                 "\t}\n" +
                                                                 "}", FlatNode.class);
        Assert.assertEquals(FlatNode.FlatNodeType.VALUE, flatNode.getType());
    }
}