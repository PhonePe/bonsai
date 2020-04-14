package com.phonepe.platform.bonsai.models.blocks.delta;

import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.models.blocks.delta.type.DeltaOperationType;
import com.phonepe.platform.bonsai.models.blocks.delta.visitor.DeltaOperationBiConsumerVisitor;
import com.phonepe.platform.bonsai.models.blocks.delta.visitor.DeltaOperationVisitor;
import com.phonepe.platform.bonsai.models.blocks.delta.visitor.DeltaOperationVoidVisitor;
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
    public void accept(DeltaOperationVoidVisitor deltaOperationVoidVisitor) {
        deltaOperationVoidVisitor.visit(this);
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
