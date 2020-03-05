package com.phonepe.platform.bonsai.models.blocks.delta;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.phonepe.platform.bonsai.models.blocks.delta.type.DeltaOperationType;
import com.phonepe.platform.bonsai.models.blocks.delta.visitor.DeltaOperationVisitor;
import com.phonepe.platform.bonsai.models.blocks.delta.visitor.DeltaOperationVoidVisitor;
import lombok.Data;

/**
 * An abstract class holds the details of kind of input given to chimera-bulk-input/output APIs used for tree formation.
 * @author suraj.s
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "deltaOperationType")
@JsonSubTypes({
        @JsonSubTypes.Type(name = "KEY_MAPPING_DELTA", value = KeyMappingDeltaOperation.class),
        @JsonSubTypes.Type(name = "KNOT_DELTA", value = KnotDeltaOperation.class),
        @JsonSubTypes.Type(name = "EDGE_DELTA", value = EdgeDeltaOperation.class)
})
public abstract class DeltaOperation {

    private DeltaOperationType deltaOperationType;

    protected DeltaOperation(final DeltaOperationType deltaOperationType) {
        this.deltaOperationType = deltaOperationType;
    }

    public abstract <T> T accept(T t, DeltaOperationVisitor<T> deltaOperationVisitor);

    public abstract void accept(DeltaOperationVoidVisitor deltaOperationVoidVisitor);
}
