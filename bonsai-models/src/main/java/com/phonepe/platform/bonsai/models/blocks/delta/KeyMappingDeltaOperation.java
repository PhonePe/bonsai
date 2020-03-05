package com.phonepe.platform.bonsai.models.blocks.delta;

import com.phonepe.platform.bonsai.models.blocks.delta.type.DeltaOperationType;
import com.phonepe.platform.bonsai.models.blocks.delta.visitor.DeltaOperationVisitor;
import com.phonepe.platform.bonsai.models.blocks.delta.visitor.DeltaOperationVoidVisitor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class KeyMappingDeltaOperation extends DeltaOperation {

    private String keyId;
    private String knotId;

    public KeyMappingDeltaOperation() {
        super(DeltaOperationType.KEY_MAPPING_DELTA);
    }

    @Builder
    public KeyMappingDeltaOperation(final String keyId, final String knotId) {
        super(DeltaOperationType.KEY_MAPPING_DELTA);
        this.keyId  = keyId;
        this.knotId = knotId;
    }

    @Override
    public <T> T accept(T t, DeltaOperationVisitor<T> deltaOperationVisitor) {
        return deltaOperationVisitor.visit(t, this);
    }

    @Override
    public void accept(DeltaOperationVoidVisitor deltaOperationVoidVisitor) {
        deltaOperationVoidVisitor.visit(this);
    }
}
