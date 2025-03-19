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

package com.phonepe.commons.bonsai.models.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.phonepe.commons.bonsai.models.value.BooleanValue;
import com.phonepe.commons.bonsai.models.value.ByteValue;
import com.phonepe.commons.bonsai.models.value.JsonValue;
import com.phonepe.commons.bonsai.models.value.NumberValue;
import com.phonepe.commons.bonsai.models.value.ObjectValue;
import com.phonepe.commons.bonsai.models.value.StringValue;
import com.phonepe.commons.bonsai.models.value.Value;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ValuedKnotData extends KnotData {
    private Value value;

    public ValuedKnotData() {
        super(KnotDataType.VALUED);
    }

    @Builder
    public ValuedKnotData(Value value) {
        super(KnotDataType.VALUED);
        this.value = value;
    }

    public static ValuedKnotData stringValue(String data) {
        return new ValuedKnotData(new StringValue(data));
    }

    public static ValuedKnotData numberValue(Number data) {
        return new ValuedKnotData(new NumberValue(data));
    }

    public static ValuedKnotData booleanValue(boolean data) {
        return new ValuedKnotData(new BooleanValue(data));
    }

    public static ValuedKnotData byteValue(byte[] data) {
        return new ValuedKnotData(new ByteValue(data));
    }

    public static ValuedKnotData jsonValue(JsonNode data) {
        return new ValuedKnotData(new JsonValue(data));
    }

    public static ValuedKnotData objectValue(Object data) {
        return new ValuedKnotData(new ObjectValue(data));
    }

    @Override
    public <T> T accept(KnotDataVisitor<T> knotDataVisitor) {
        return knotDataVisitor.visit(this);
    }
}
