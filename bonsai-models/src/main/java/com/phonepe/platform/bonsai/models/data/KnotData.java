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

package com.phonepe.platform.bonsai.models.data;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.Getter;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "knotDataType")
@JsonSubTypes({
        @JsonSubTypes.Type(name = "VALUED", value = ValuedKnotData.class),
        @JsonSubTypes.Type(name = "MULTI_KNOT", value = MultiKnotData.class),
        @JsonSubTypes.Type(name = "MAP_KNOT", value = MapKnotData.class)
})
public abstract class KnotData {

    private KnotDataType knotDataType;

    protected KnotData(KnotDataType knotDataType) {
        this.knotDataType = knotDataType;
    }

    public abstract <T> T accept(KnotDataVisitor<T> knotDataVisitor);

    public enum KnotDataType {
        VALUED(false),
        MULTI_KNOT(true),
        MAP_KNOT(true);

        @Getter
        private boolean isDeReferenced;

        KnotDataType(boolean isDeReferenced) {
            this.isDeReferenced = isDeReferenced;
        }
    }
}
