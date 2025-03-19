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

package com.phonepe.commons.bonsai.models.value;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "valueType")
@JsonSubTypes({
        @JsonSubTypes.Type(name = "NUMBER", value = NumberValue.class),
        @JsonSubTypes.Type(name = "STRING", value = StringValue.class),
        @JsonSubTypes.Type(name = "BOOLEAN", value = BooleanValue.class),
        @JsonSubTypes.Type(name = "JSON", value = JsonValue.class),
        @JsonSubTypes.Type(name = "BYTE", value = ByteValue.class),
        @JsonSubTypes.Type(name = "OBJECT", value = ObjectValue.class)
})
public abstract class Value {
    private ValueType valueType;

    protected Value(ValueType valueType) {
        this.valueType = valueType;
    }

    public abstract <T> T accept(ValueVisitor<T> valueVisitor);

    public enum ValueType {
        NUMBER,
        STRING,
        BOOLEAN,
        JSON,
        BYTE,
        OBJECT
    }
}
