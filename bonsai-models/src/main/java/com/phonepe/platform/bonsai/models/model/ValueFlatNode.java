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

package com.phonepe.platform.bonsai.models.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.phonepe.platform.bonsai.models.value.BooleanValue;
import com.phonepe.platform.bonsai.models.value.ByteValue;
import com.phonepe.platform.bonsai.models.value.JsonValue;
import com.phonepe.platform.bonsai.models.value.NumberValue;
import com.phonepe.platform.bonsai.models.value.ObjectValue;
import com.phonepe.platform.bonsai.models.value.StringValue;
import com.phonepe.platform.bonsai.models.value.Value;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ValueFlatNode extends FlatNode {

    private Value value;

    public ValueFlatNode() {
        super(FlatNodeType.VALUE);
    }

    public ValueFlatNode(Value value) {
        super(FlatNodeType.VALUE);
        this.value = value;
    }

    public static ValueFlatNode stringValue(String data) {
        return new ValueFlatNode(new StringValue(data));
    }

    public static ValueFlatNode numberValue(Number data) {
        return new ValueFlatNode(new NumberValue(data));
    }

    public static ValueFlatNode booleanValue(boolean data) {
        return new ValueFlatNode(new BooleanValue(data));
    }

    public static ValueFlatNode byteValue(byte[] data) {
        return new ValueFlatNode(new ByteValue(data));
    }

    public static ValueFlatNode jsonValue(JsonNode data) {
        return new ValueFlatNode(new JsonValue(data));
    }

    public static ValueFlatNode objectValue(Object data) {
        return new ValueFlatNode(new ObjectValue(data));
    }

    @Override
    public <T> T accept(FlatNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
