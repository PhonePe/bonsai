package com.phonepe.platform.bonsai.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.jknack.handlebars.internal.Files;

import java.io.IOException;

/**
 * @author tushar.naik
 * @version 1.0  03/11/17 - 3:24 PM
 */
public class ObjectExtractor {

    public <T> T getObject(String resource, Class<T> clazz) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        String read = Files.read(classLoader.getResourceAsStream(resource));
        return Mapper.MAPPER.readValue(read, clazz);
    }

    public <T> T getObject(String resource, TypeReference<T> typeReference) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        String read = Files.read(classLoader.getResourceAsStream(resource));
        return Mapper.MAPPER.readValue(read, typeReference);
    }
}
