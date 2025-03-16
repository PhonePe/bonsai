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

package com.phonepe.platform.bonsai.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.annotations.VisibleForTesting;
import com.phonepe.platform.bonsai.models.value.BooleanValue;
import com.phonepe.platform.bonsai.models.value.ByteValue;
import com.phonepe.platform.bonsai.models.value.JsonValue;
import com.phonepe.platform.bonsai.models.value.NumberValue;
import com.phonepe.platform.bonsai.models.value.ObjectValue;
import com.phonepe.platform.bonsai.models.value.StringValue;
import com.phonepe.platform.bonsai.models.value.Value;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ValueNode extends Node {
    private Value value;

    public ValueNode() {
        super(NodeType.VALUE, null, 0);
    }

    @Builder
    public ValueNode(String id, long version, Value value) {
        super(NodeType.VALUE, id, version);
        this.value = value;
    }

    @VisibleForTesting
    public static ValueNode stringValue(String data) {
        return new ValueNode(null, 0, new StringValue(data));
    }

    @VisibleForTesting
    public static ValueNode numberValue(Number data) {
        return new ValueNode(null, 0, new NumberValue(data));
    }

    @VisibleForTesting
    public static ValueNode booleanValue(boolean data) {
        return new ValueNode(null, 0, new BooleanValue(data));
    }

    @VisibleForTesting
    public static ValueNode byteValue(byte[] data) {
        return new ValueNode(null, 0, new ByteValue(data));
    }

    @VisibleForTesting
    public static ValueNode jsonValue(JsonNode data) {
        return new ValueNode(null, 0, new JsonValue(data));
    }

    @VisibleForTesting
    public static ValueNode objectValue(Object data) {
        return new ValueNode(null, 0, new ObjectValue(data));
    }

    @Override
    public <T> T accept(NodeVisitor<T> nodeVisitor) {
        return nodeVisitor.visit(this);
    }

}
