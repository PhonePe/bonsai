package com.phonepe.platform.bonsai.models.blocks.delta.visitor;

import com.phonepe.platform.bonsai.models.blocks.delta.EdgeDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.KeyMappingDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.KnotDeltaOperation;

public interface DeltaOperationVisitor<T> {

    T visit(T t, KeyMappingDeltaOperation keyMappingDeltaOperation);

    T visit(T t, KnotDeltaOperation knotDeltaOperation);

    T visit(T t, EdgeDeltaOperation edgeDeltaOperation);
}
