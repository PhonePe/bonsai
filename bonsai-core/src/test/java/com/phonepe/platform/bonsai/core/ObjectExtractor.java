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

package com.phonepe.platform.bonsai.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Preconditions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ObjectExtractor {

    private static String read(InputStream source) throws IOException {
        return read(new InputStreamReader(source, StandardCharsets.UTF_8));
    }

    private static String read(Reader source) throws IOException {
        return read(new BufferedReader(source));
    }

    private static String read(BufferedReader source) throws IOException {
        Preconditions.checkNotNull(source, "The input is required.");
        try (source) {
            int read = source.read();
            StringBuilder script;
            for (script = new StringBuilder(); read != -1; read = source.read()) {
                script.append((char) read);
            }
            return script.toString();
        }
    }

    public <T> T getObject(String resource, Class<T> clazz) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        String read = read(classLoader.getResourceAsStream(resource));
        return Parsers.MAPPER.readValue(read, clazz);
    }

    public <T> T getObject(String resource, TypeReference<T> typeReference) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        String read = read(classLoader.getResourceAsStream(resource));
        return Parsers.MAPPER.readValue(read, typeReference);
    }
}
