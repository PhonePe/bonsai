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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(name = "VALUE", value = ValueFlatNode.class),
        @JsonSubTypes.Type(name = "LIST", value = ListFlatNode.class),
        @JsonSubTypes.Type(name = "MAP", value = MapFlatNode.class)
})
public abstract class FlatNode {
    private FlatNodeType type;

    public FlatNode(FlatNodeType type) {
        this.type = type;
    }

    public abstract <T> T accept(FlatNodeVisitor<T> visitor);

    public enum FlatNodeType {
        VALUE,
        LIST,
        MAP
    }
}
