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

package com.phonepe.commons.bonsai.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import lombok.experimental.UtilityClass;

import java.io.InputStream;

@UtilityClass
public class Parsers {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    public static ParseContext DOCUMENT_PARSER = JsonPath.using(Configuration.builder()
            .jsonProvider(new JacksonJsonProvider())
            .mappingProvider(new JacksonMappingProvider())
            .options(Option.SUPPRESS_EXCEPTIONS,
                    Option.ALWAYS_RETURN_LIST,
                    Option.DEFAULT_PATH_LEAF_TO_NULL)
            .build());

    static {
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public DocumentContext parse(String var1) {
        return DOCUMENT_PARSER.parse(var1);
    }

    public DocumentContext parse(Object var1) {
        return DOCUMENT_PARSER.parse(var1);
    }

    public DocumentContext parse(InputStream var1) {
        return DOCUMENT_PARSER.parse(var1);
    }
}
