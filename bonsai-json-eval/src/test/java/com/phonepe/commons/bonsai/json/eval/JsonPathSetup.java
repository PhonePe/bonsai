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

package com.phonepe.commons.bonsai.json.eval;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

import java.util.HashSet;
import java.util.Set;

public interface JsonPathSetup {

    /**
     * using jackson json providers, to allow mapping into a pojo also, suppressing all exceptions
     * also, jsonPath will always return a list
     */
    static void setup() {
        Configuration.Defaults defaults = new Configuration.Defaults() {
            private final JsonProvider jsonProvider = new JacksonJsonProvider();
            private final MappingProvider mappingProvider = new JacksonMappingProvider();

            @Override
            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            @Override
            public MappingProvider mappingProvider() {
                return mappingProvider;
            }

            @Override
            public Set<Option> options() {
                HashSet<Option> options = new HashSet<>();
                options.add(Option.SUPPRESS_EXCEPTIONS);
                options.add(Option.ALWAYS_RETURN_LIST);
                options.add(Option.DEFAULT_PATH_LEAF_TO_NULL);
                return options;
            }
        };
        Configuration.setDefaults(defaults);
    }
}
