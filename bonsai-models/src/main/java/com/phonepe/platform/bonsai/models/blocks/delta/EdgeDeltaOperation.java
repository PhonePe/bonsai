package com.phonepe.platform.bonsai.models.blocks.delta;

import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.models.blocks.delta.type.DeltaOperationType;
import com.phonepe.platform.bonsai.models.blocks.delta.visitor.DeltaOperationVisitor;
import com.phonepe.platform.bonsai.models.blocks.delta.visitor.DeltaOperationVoidVisitor;
import lombok.Data;

@Data
public class EdgeDeltaOperation extends DeltaOperation {

    private Edge edge;

    public EdgeDeltaOperation() {
        super(DeltaOperationType.EDGE_DATA);
    }

    public EdgeDeltaOperation(final Edge edge) {
        super(DeltaOperationType.EDGE_DATA);
        this.edge = edge;
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
