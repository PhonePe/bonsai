package com.phonepe.platform.bonsai.models.blocks.delta;

import com.phonepe.platform.bonsai.models.blocks.delta.type.DeltaOperationType;
import com.phonepe.platform.bonsai.models.blocks.delta.visitor.DeltaOperationVisitor;
import com.phonepe.platform.bonsai.models.blocks.delta.visitor.DeltaOperationVoidVisitor;
import lombok.Data;

@Data
public class KeyMappingDeltaOperation extends DeltaOperation {

    private String keyId;
    private String knotId;

    public KeyMappingDeltaOperation() {
        super(DeltaOperationType.KEY_MAPPING_DATA);
    }

    public KeyMappingDeltaOperation(final String keyId, final String knotId) {
        super(DeltaOperationType.KEY_MAPPING_DATA);
        this.keyId  = keyId;
        this.knotId = knotId;
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
