package com.phonepe.platform.bonsai.core.vital;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

import java.util.HashSet;
import java.util.Set;

/**
 * @author tushar.naik
 * @version 1.0  20/06/18 - 7:12 PM
 */
public interface JsonPathSetup {

    /**
     * using jackson json providers, to allow mapping into a pojo
     * also, suppressing all exceptions
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
