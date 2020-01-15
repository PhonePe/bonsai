package com.phonepe.platform.bonsai.models.blocks.delta;

import com.phonepe.platform.bonsai.models.blocks.Knot;
import com.phonepe.platform.bonsai.models.blocks.delta.type.DeltaOperationType;
import com.phonepe.platform.bonsai.models.blocks.delta.visitor.DeltaOperationVisitor;
import com.phonepe.platform.bonsai.models.blocks.delta.visitor.DeltaOperationVoidVisitor;
import lombok.Data;

@Data
public class KnotDeltaOperation extends DeltaOperation {

    private Knot knot;

    public KnotDeltaOperation() {
        super(DeltaOperationType.KNOT_DATA);
    }

    public KnotDeltaOperation(final Knot knot) {
        super(DeltaOperationType.KNOT_DATA);
        this.knot = knot;
    }

    @Override
    public <T> T addIntoTree(T t, DeltaOperationVisitor<T> deltaOperationVisitor) {
        return deltaOperationVisitor.visit(t, this);
    }

    @Override
    public void saveIntoDataStore(DeltaOperationVoidVisitor deltaOperationVoidVisitor) {
        deltaOperationVoidVisitor.visit(this);
    }
}
