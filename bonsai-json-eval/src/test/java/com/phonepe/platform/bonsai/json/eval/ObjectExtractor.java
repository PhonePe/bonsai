package com.phonepe.platform.bonsai.json.eval;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.*;
import java.nio.charset.Charset;

/**
 * @author tushar.naik
 * @version 1.0  03/11/17 - 3:24 PM
 */
public class ObjectExtractor {

    public <T> T getObject(String resource, Class<T> clazz) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        String read = read(classLoader.getResourceAsStream(resource));
        return Mapper.MAPPER.readValue(read, clazz);
    }

    public <T> T getObject(String resource, TypeReference<T> typeReference) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        String read = read(classLoader.getResourceAsStream(resource));
        return Mapper.MAPPER.readValue(read, typeReference);
    }

    private static String read(InputStream source) throws IOException {
        return read(new InputStreamReader(source, Charset.forName("UTF-8")));
    }

    private static String read(Reader source) throws IOException {
        return read(new BufferedReader(source));
    }

    private static String read(BufferedReader source) throws IOException {
        try {
            int read = source.read();
            StringBuilder script;
            for (script = new StringBuilder(); read != -1; read = source.read()) {
                script.append((char) read);
            }
            return script.toString();
        } finally {
            source.close();
        }
    }
}
