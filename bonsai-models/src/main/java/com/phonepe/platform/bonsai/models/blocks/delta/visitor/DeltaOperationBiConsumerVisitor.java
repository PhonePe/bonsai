package com.phonepe.platform.bonsai.models.blocks.delta.visitor;

import com.phonepe.platform.bonsai.models.blocks.delta.EdgeDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.KeyMappingDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.KnotDeltaOperation;

/**
 * @author - suraj.s
 * @date - 2020-04-13
 */
public interface DeltaOperationBiConsumerVisitor<T, U> {
    T visit(T t, U u, KeyMappingDeltaOperation keyMappingDeltaOperation);

    T visit(T t, U u, KnotDeltaOperation knotDeltaOperation);

    T visit(T t, U u, EdgeDeltaOperation edgeDeltaOperation);
}
