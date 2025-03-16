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

package com.phonepe.platform.bonsai.models.blocks.delta;

import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.models.blocks.delta.type.DeltaOperationType;
import com.phonepe.platform.bonsai.models.blocks.delta.visitor.DeltaOperationBiConsumerVisitor;
import com.phonepe.platform.bonsai.models.blocks.delta.visitor.DeltaOperationUnaryVisitor;
import com.phonepe.platform.bonsai.models.blocks.delta.visitor.DeltaOperationVisitor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EdgeDeltaOperation extends DeltaOperation {

    private Edge edge;

    public EdgeDeltaOperation() {
        super(DeltaOperationType.EDGE_DELTA);
    }

    @Builder
    public EdgeDeltaOperation(final Edge edge) {
        super(DeltaOperationType.EDGE_DELTA);
        this.edge = edge;
    }

    @Override
    public <T> T accept(DeltaOperationUnaryVisitor<T> deltaOperationUnaryVisitor) {
        return deltaOperationUnaryVisitor.visit(this);
    }

    @Override
    public <T> T accept(T t, DeltaOperationVisitor<T> deltaOperationVisitor) {
        return deltaOperationVisitor.visit(t, this);
    }

    @Override
    public <T, U> void accept(T t, U u, DeltaOperationBiConsumerVisitor<T, U> deltaOperationBiConsumerVisitor) {
        deltaOperationBiConsumerVisitor.visit(t, u, this);
    }
}
