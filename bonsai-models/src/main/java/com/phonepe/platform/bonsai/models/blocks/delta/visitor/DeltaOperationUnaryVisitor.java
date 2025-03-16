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

package com.phonepe.platform.bonsai.models.blocks.delta.visitor;

import com.phonepe.platform.bonsai.models.blocks.delta.EdgeDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.KeyMappingDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.KnotDeltaOperation;

public interface DeltaOperationUnaryVisitor<T> {
    /**
     * Visitor function signature to save KeyMappingDeltaOperation into KeyStore.
     *
     * @param keyMappingDeltaOperation - {@link KeyMappingDeltaOperation} object; it contains key and knotId mapping.
     */
    T visit(KeyMappingDeltaOperation keyMappingDeltaOperation);

    /**
     * Visitor function signature to save KnotDeltaOperation into KnotStore.
     *
     * @param knotDeltaOperation -  {@link KnotDeltaOperation} object; it contains all the data to represent a single knot and
     *                           its corresponding edgeIds.
     */
    T visit(KnotDeltaOperation knotDeltaOperation);

    /**
     * Visitor function signature to save EdgeDeltaOperation into EdgeStore.
     *
     * @param edgeDeltaOperation - {@link EdgeDeltaOperation} object; it contains all the data to represent a single edge and
     *                           its corresponding child knotId.
     */
    T visit(EdgeDeltaOperation edgeDeltaOperation);
}
