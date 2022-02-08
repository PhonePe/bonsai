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
