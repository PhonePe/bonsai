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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jayway.jsonpath.TypeRef;
import com.phonepe.commons.query.dsl.Filter;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Data
@NoArgsConstructor
@Slf4j
public class PathExpression {

    public static final TypeRef<List<Number>> NUMBER_TYPE_REF = new TypeRef<>() {
    };
    public static final TypeRef<List<Object>> OBJECT_TYPE_REF = new TypeRef<>() {
    };

    @JsonProperty
    private String key;

    @JsonProperty
    private String path;

    /* is the value that is being looked at, multivalued or not */
    private boolean multivalued = false;

    /* operations done on multi-valued entities from path */
    @JsonProperty
    private Operation operation;

    /* adjustments done on the value obtained from path */
    @JsonProperty
    private List<Adjustment> adjustments;

    private String type;

    /* filters evaluated if not null */
    private List<Filter> filters;

    /* if value is passed and all filters pass, this will be returned (applicable only when adjustments and path arent present) */
    private Object value;

    @Override
    public String toString() {
        return "[" + "key:'" + key + '\'' +
                ", path:'" + path + '\'' +
                ", adjustments:'" + adjustments + '\'' +
                ", operation:" + operation +
                ']';
    }

    public enum Operation {
        SUM,
        AVERAGE,
        MAX,
        MIN,
        LENGTH,
        PAD_TIMESTAMP,
        CONVERT_TO_DATE
    }

    public static class Adjustment {

        @JsonProperty
        Type type;

        @JsonProperty
        Number value;

        public double reValue(Number initialValue) {
            return switch (type) {
                case ADD -> initialValue.doubleValue() + value.doubleValue();
                case DIVIDE -> initialValue.doubleValue() / value.doubleValue();
                case SUBTRACT -> initialValue.doubleValue() - value.doubleValue();
                case MULTIPLY -> initialValue.doubleValue() * value.doubleValue();
                case SQRT -> Math.sqrt(initialValue.doubleValue());
                case CEIL -> Math.ceil(initialValue.doubleValue());
                case FLOOR -> Math.floor(initialValue.doubleValue());
                case POW -> Math.pow(initialValue.doubleValue(), value.doubleValue());
                default -> throw new UnsupportedOperationException("Adjustment not supported: " + this.toString());
            };
        }

        enum Type {
            ADD,
            DIVIDE,
            SUBTRACT,
            MULTIPLY,
            SQRT,
            CEIL,
            FLOOR,
            POW
        }
    }
}
