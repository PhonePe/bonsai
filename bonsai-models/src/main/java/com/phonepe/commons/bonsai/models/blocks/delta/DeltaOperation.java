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

package com.phonepe.commons.bonsai.models.blocks.delta;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.phonepe.commons.bonsai.models.blocks.delta.type.DeltaOperationType;
import com.phonepe.commons.bonsai.models.blocks.delta.visitor.DeltaOperationBiConsumerVisitor;
import com.phonepe.commons.bonsai.models.blocks.delta.visitor.DeltaOperationUnaryVisitor;
import com.phonepe.commons.bonsai.models.blocks.delta.visitor.DeltaOperationVisitor;
import lombok.Data;

/**
 * An abstract class holds the details of kind of input given to chimera-bulk-input/output APIs used for tree formation.
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "deltaOperationType")
@JsonSubTypes({
        @JsonSubTypes.Type(name = "KEY_MAPPING_DELTA", value = KeyMappingDeltaOperation.class),
        @JsonSubTypes.Type(name = "KNOT_DELTA", value = KnotDeltaOperation.class),
        @JsonSubTypes.Type(name = "EDGE_DELTA", value = EdgeDeltaOperation.class)
})
public abstract class DeltaOperation {

    private DeltaOperationType deltaOperationType;

    protected DeltaOperation(final DeltaOperationType deltaOperationType) {
        this.deltaOperationType = deltaOperationType;
    }

    public abstract <T> T accept(DeltaOperationUnaryVisitor<T> deltaOperationUnaryVisitor);

    public abstract <T> T accept(T t, DeltaOperationVisitor<T> deltaOperationVisitor);

    public abstract <T, U> void accept(T t, U u, DeltaOperationBiConsumerVisitor<T, U> deltaOperationBiConsumerVisitor);

}
